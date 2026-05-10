package com.focussphere.controller;

import com.focussphere.model.Notification;
import com.focussphere.model.User;
import com.focussphere.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/api/notifications/{notificationId}/read")
    @ResponseBody
    public String markNotificationAsRead(@PathVariable Long notificationId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "{\"success\": false, \"message\": \"Not authenticated\"}";
        }

        try {
            Notification notification = notificationService.getNotification(notificationId);
            if (notification == null) {
                return "{\"success\": false, \"message\": \"Notification not found\"}";
            }

            if (!notification.getRecipient().getId().equals(sessionUser.getId())) {
                return "{\"success\": false, \"message\": \"Unauthorized\"}";
            }

            notificationService.markAsRead(notificationId);
            return "{\"success\": true, \"message\": \"Notification marked as read\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    @PostMapping("/api/notifications/{notificationId}/delete")
    @ResponseBody
    public String deleteNotification(@PathVariable Long notificationId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "{\"success\": false, \"message\": \"Not authenticated\"}";
        }

        try {
            Notification notification = notificationService.getNotification(notificationId);
            if (notification == null) {
                return "{\"success\": false, \"message\": \"Notification not found\"}";
            }

            if (!notification.getRecipient().getId().equals(sessionUser.getId())) {
                return "{\"success\": false, \"message\": \"Unauthorized\"}";
            }

            notificationService.deleteNotification(notificationId);
            return "{\"success\": true, \"message\": \"Notification deleted\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/api/notifications/unread-count")
    @ResponseBody
    public String getUnreadCount(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "{\"count\": 0}";
        }

        long unreadCount = notificationService.getUnreadNotificationCount(sessionUser);
        return "{\"count\": " + unreadCount + "}";
    }
}
