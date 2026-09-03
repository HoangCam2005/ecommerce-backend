package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.ApiResponse;
import com.example.ecommercebackend.dto.CartItemRequest;
import com.example.ecommercebackend.dto.CartResponse;
import com.example.ecommercebackend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        CartResponse cart = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Lay gio hang thanh cong", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addOrUpdateItem(Authentication authentication,
                                                                       @Valid @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addOrUpdateItem(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Cap nhat gio hang thanh cong", cart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(Authentication authentication,
                                                          @PathVariable Long productId) {
        cartService.removeItem(authentication.getName(), productId);
        return ResponseEntity.ok(ApiResponse.success("Xoa san pham khoi gio hang thanh cong", null));
    }
}
