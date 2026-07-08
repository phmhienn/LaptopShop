package com.laptopstore.application.facade;

import com.laptopstore.application.dto.other.NotificationDTO;
import com.laptopstore.application.mapper.NotificationMapper;
import com.laptopstore.application.security.services.UserDetailsImpl;
import com.laptopstore.business.service.NotificationService;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    public PagedResponse<NotificationDTO> getMyNotifications(int page, int size, String sortBy, String sortDir) {
        Long userId = getCurrentUserId();
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        Page<Notification> notificationsPage = notificationService.getUserNotifications(userId, pageable);
        return mapToPagedResponse(notificationsPage);
    }

    public PagedResponse<NotificationDTO> getMyUnreadNotifications(int page, int size, String sortBy, String sortDir) {
        Long userId = getCurrentUserId();
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        Page<Notification> notificationsPage = notificationService.getUnreadNotifications(userId, pageable);
        return mapToPagedResponse(notificationsPage);
    }

    public long countUnreadNotifications() {
        Long userId = getCurrentUserId();
        return notificationService.countUnreadNotifications(userId);
    }

    public NotificationDTO markAsRead(Long notificationId) {
        Long userId = getCurrentUserId();
        return notificationMapper.toNotificationDTO(notificationService.markAsRead(notificationId, userId));
    }

    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
    }

    public void deleteNotification(Long notificationId) {
        Long userId = getCurrentUserId();
        notificationService.deleteNotification(notificationId, userId);
    }

    // Helper methods
    
    private Pageable getPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PagedResponse<NotificationDTO> mapToPagedResponse(Page<Notification> notificationsPage) {
        List<NotificationDTO> content = notificationsPage.getContent().stream()
                .map(notificationMapper::toNotificationDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, notificationsPage.getNumber(), notificationsPage.getSize(),
                notificationsPage.getTotalElements(), notificationsPage.getTotalPages());
    }
}
