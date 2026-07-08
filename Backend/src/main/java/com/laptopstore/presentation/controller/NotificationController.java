package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.other.NotificationDTO;
import com.laptopstore.application.facade.NotificationFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationFacade notificationFacade;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationDTO>>> getMyNotifications(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<NotificationDTO> response = notificationFacade.getMyNotifications(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationDTO>>> getMyUnreadNotifications(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<NotificationDTO> response = notificationFacade.getMyUnreadNotifications(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/unread/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications() {
        return ResponseEntity.ok(ApiResponse.success(notificationFacade.countUnreadNotifications()));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsRead(@PathVariable Long id) {
        NotificationDTO notification = notificationFacade.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        notificationFacade.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationFacade.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully"));
    }
}
