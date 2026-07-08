package com.laptopstore.application.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String orderCode;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    
    private String note;
    
    private List<OrderItemDTO> items;
    private PaymentDTO payment;
    private ShipmentDTO shipment;
    
    private LocalDateTime createdAt;
}
