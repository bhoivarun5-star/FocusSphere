package com.focussphere.repository;

import com.focussphere.model.User;
import com.focussphere.model.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByRollNo(String rollNo);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByRole(UserRole role);

    long countByRole(UserRole role);

    @Modifying
    @Query("delete from User u where u.role = :role")
    int deleteByRole(@Param("role") UserRole role);
}
