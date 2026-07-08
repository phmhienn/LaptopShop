package com.laptopstore.application.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private String method;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private LocalDateTime paidAt;
}
