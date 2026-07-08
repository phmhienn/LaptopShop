package com.laptopstore.application.dto.other;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private String referenceId;
    private LocalDateTime createdAt;
}
