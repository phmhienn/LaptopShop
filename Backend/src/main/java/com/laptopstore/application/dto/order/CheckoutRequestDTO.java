package com.laptopstore.application.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequestDTO {
    @NotBlank(message = "Shipping name is required")
    private String shippingName;

    @NotBlank(message = "Shipping phone is required")
    private String shippingPhone;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String note;
    
    private String paymentMethod;
    
    private String couponCode;
}
