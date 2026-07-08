package com.laptopstore.application.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
