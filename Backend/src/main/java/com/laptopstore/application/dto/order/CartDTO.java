package com.laptopstore.application.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
}
