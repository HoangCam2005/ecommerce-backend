package com.example.ecommercebackend.controller;

import com.example.ecommercebackend.dto.ApiResponse;
import com.example.ecommercebackend.dto.OrderResponse;
import com.example.ecommercebackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(Authentication authentication) {
        OrderResponse order = orderService.checkout(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dat hang thanh cong", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(Authentication authentication) {
        List<OrderResponse> orders = orderService.getOrdersByUsername(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Lay danh sach don hang thanh cong", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id, Authentication authentication) {
        OrderResponse order = orderService.getOrderById(id, authentication.getName(),
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
        return ResponseEntity.ok(ApiResponse.success("Lay don hang thanh cong", order));
    }
}
