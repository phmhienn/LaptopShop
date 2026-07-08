package com.laptopstore.business.service;

import com.laptopstore.common.enums.NotificationType;
import com.laptopstore.data.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    
    Notification sendNotification(Long userId, String title, String message, NotificationType type, String referenceId);
    
    Notification markAsRead(Long notificationId, Long userId);
    
    void markAllAsRead(Long userId);
    
    Page<Notification> getUserNotifications(Long userId, Pageable pageable);
    
    Page<Notification> getUnreadNotifications(Long userId, Pageable pageable);
    
    long countUnreadNotifications(Long userId);
    
    void deleteNotification(Long notificationId, Long userId);
}
