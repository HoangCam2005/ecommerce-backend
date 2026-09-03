package com.example.ecommercebackend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Ten san pham khong duoc de trong")
    private String name;

    @NotNull(message = "Gia san pham khong duoc de trong")
    @DecimalMin(value = "0.0", inclusive = true, message = "Gia san pham phai lon hon hoac bang 0")
    private BigDecimal price;

    @NotNull(message = "So luong ton kho khong duoc de trong")
    @Min(value = 0, message = "So luong ton kho phai lon hon hoac bang 0")
    private Integer stock;

    @NotNull(message = "Ma danh muc khong duoc de trong")
    private Long categoryId;
}
