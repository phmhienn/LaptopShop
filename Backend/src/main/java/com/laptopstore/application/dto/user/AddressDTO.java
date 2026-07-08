package com.laptopstore.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address line is required")
    private String addressLine;

    private String ward;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "City is required")
    private String city;

    private Boolean isDefault;
}
