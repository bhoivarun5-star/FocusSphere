package com.focussphere.service;

import com.focussphere.model.Notification;
import com.focussphere.model.NotificationType;
import com.focussphere.model.Room;
import com.focussphere.model.User;
import com.focussphere.repository.NotificationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(User recipient, NotificationType type, String title, String message, Room room, User sender) {
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient cannot be null");
        }
        if (type == null || title == null || message == null) {
            throw new IllegalArgumentException("Type, title, and message are required");
        }

        Notification notification = new Notification(recipient, type, title, message, room, sender);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(User user) {
        if (user == null) {
            return List.of();
        }
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotificationsForUser(User user) {
        if (user == null) {
            return List.of();
        }
        return notificationRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false);
    }

    public long getUnreadNotificationCount(User user) {
        if (user == null) {
            return 0;
        }
        return notificationRepository.countByRecipientAndIsRead(user, false);
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public Notification getNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElse(null);
    }
}
