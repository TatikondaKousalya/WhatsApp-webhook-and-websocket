package com.chatapp.controller;

import com.chatapp.data.entity.Notification;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<List<Notification>>> userNotifications(@PathVariable Long id) {
        List<Notification> notifications = notificationService.getUserNotifications(id);
        return ResponseEntity.ok(ApiResponse.<List<Notification>>builder()
                .success(true).message("Notifications fetched successfully.")
                .data(notifications).build());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> read(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.<Notification>builder()
                .success(true).message("Notification marked as read.")
                .data(notification).build());
    }

    @PutMapping("/user/{id}/read-all")
    public ResponseEntity<ApiResponse<Void>> readAll(@PathVariable Long id) {
        notificationService.markAllAsRead(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("All notifications marked as read.").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Notification deleted successfully.").build());
    }

}