package com.chatapp.service;

import com.chatapp.data.entity.Notification;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.NotificationRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.enums.NotificationType;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Create Notification
     */
    public Notification createNotification(Long userId,
                                           String title,
                                           String message,
                                           String notificationType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.valueOf(notificationType));
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    /**
     * Get User Notifications
     */
    public List<Notification> getUserNotifications(Long userId) {

        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Mark Notification as Read
     */
    public Notification markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found."));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    /**
     * Mark All Notifications as Read
     */
    public void markAllAsRead(Long userId) {

        List<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        notifications.forEach(notification -> notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    /**
     * Delete Notification
     */
    public void deleteNotification(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found."));

        notificationRepository.delete(notification);
    }

    /**
     * Get Notification
     */
    public Notification getNotification(Long notificationId) {

        return notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found."));
    }
}