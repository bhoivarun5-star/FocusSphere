package com.focussphere.repository;

import com.focussphere.model.Room;
import com.focussphere.model.RoomSessionActivity;
import com.focussphere.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomSessionActivityRepository extends JpaRepository<RoomSessionActivity, Long> {
    List<RoomSessionActivity> findByRoomOrderBySessionStartDesc(Room room);

    Optional<RoomSessionActivity> findFirstByRoomAndUserAndSessionEndIsNullOrderBySessionStartDesc(Room room, User user);

    long countBySessionEndIsNull();

    long countBySessionStartAfter(LocalDateTime sessionStart);

    @Query("select coalesce(avg(r.durationSeconds), 0) from RoomSessionActivity r where r.sessionEnd is not null and r.durationSeconds is not null")
    Double findAverageCompletedDurationSeconds();
}