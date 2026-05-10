package com.focussphere.repository;

import com.focussphere.model.Notification;
import com.focussphere.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndIsReadOrderByCreatedAtDesc(User recipient, Boolean isRead);
    long countByRecipientAndIsRead(User recipient, Boolean isRead);
}
