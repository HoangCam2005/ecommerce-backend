package com.example.ecommercebackend.service;

import com.example.ecommercebackend.dto.CartItemRequest;
import com.example.ecommercebackend.dto.CartItemResponse;
import com.example.ecommercebackend.dto.CartResponse;
import com.example.ecommercebackend.entity.Cart;
import com.example.ecommercebackend.entity.CartItem;
import com.example.ecommercebackend.entity.Product;
import com.example.ecommercebackend.entity.User;
import com.example.ecommercebackend.exception.ResourceNotFoundException;
import com.example.ecommercebackend.repository.CartItemRepository;
import com.example.ecommercebackend.repository.CartRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import com.example.ecommercebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public CartResponse addOrUpdateItem(String username, CartItemRequest request) {
        User user = getUserByUsername(username);
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham voi id: " + request.getProductId()));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        } else {
            cartItem.setQuantity(request.getQuantity());
        }

        cartItemRepository.save(cartItem);

        Cart refreshedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay gio hang"));

        return mapToResponse(refreshedCart);
    }

    public CartResponse getCart(String username) {
        User user = getUserByUsername(username);
        Cart cart = getOrCreateCart(user);
        return mapToResponse(cart);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeItem(String username, Long productId) {
        User user = getUserByUsername(username);
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham trong gio hang"));

        cartItemRepository.delete(cartItem);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung: " + username));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }
}
