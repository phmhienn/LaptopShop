package com.laptopstore.application.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BrandDTO {
    private Long id;
    private String name;
    private String slug;
    private String logo;
    private String description;
    private Boolean status;
    private LocalDateTime createdAt;
}
