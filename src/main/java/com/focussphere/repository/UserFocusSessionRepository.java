package com.focussphere.repository;

import com.focussphere.model.User;
import com.focussphere.model.UserFocusSession;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFocusSessionRepository extends JpaRepository<UserFocusSession, Long> {
    List<UserFocusSession> findByUserOrderBySessionDateDescIdDesc(User user);
}
