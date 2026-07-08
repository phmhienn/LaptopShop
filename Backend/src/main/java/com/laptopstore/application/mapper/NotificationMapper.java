package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.other.NotificationDTO;
import com.laptopstore.data.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toNotificationDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setIsRead(notification.getIsRead());
        dto.setReferenceId(notification.getReferenceId());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }
}
