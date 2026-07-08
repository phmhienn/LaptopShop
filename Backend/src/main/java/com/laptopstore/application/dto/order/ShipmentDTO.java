package com.laptopstore.application.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentDTO {
    private Long id;
    private Long orderId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String trackingNumber;
    private String shippingProvider;
    private java.math.BigDecimal shippingFee;
    private String status;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
