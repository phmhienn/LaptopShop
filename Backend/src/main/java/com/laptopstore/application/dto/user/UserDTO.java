package com.laptopstore.application.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private Boolean enabled;
    private List<String> roles;
    private LocalDateTime createdAt;
}
