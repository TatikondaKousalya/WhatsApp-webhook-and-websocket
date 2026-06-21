package com.chatapp.controller;

import com.chatapp.data.entity.Notification;
import com.chatapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{id}")
    public List<Notification> userNotifications(@PathVariable Long id){
        return notificationService.getUserNotifications(id);
    }



    @PutMapping("/{id}/read")
    public Notification read(@PathVariable Long id){
        return notificationService.markAsRead(id);
    }

    @PutMapping("/user/{id}/read-all")
    public String readAll(@PathVariable Long id){
        notificationService.markAllAsRead(id);
        return "All Read";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        notificationService.deleteNotification(id);
        return "Deleted";
    }

}