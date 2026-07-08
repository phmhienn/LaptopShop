package com.laptopstore.application.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;
    
    private String address;
    
    private String currentPassword;
    
    private String newPassword;
}
