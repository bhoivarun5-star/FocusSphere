package com.focussphere.repository;

import com.focussphere.model.Room;
import com.focussphere.model.RoomSchedule;
import com.focussphere.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomScheduleRepository extends JpaRepository<RoomSchedule, Long> {
    List<RoomSchedule> findByRoomOrderByScheduleDateDescScheduleTimeDesc(Room room);

    Optional<RoomSchedule> findByIdAndRoomAndCreatedBy(Long id, Room room, User createdBy);

    long countByScheduleDateGreaterThanEqual(LocalDate scheduleDate);

    long countByScheduleDate(LocalDate scheduleDate);

    long countByScheduleDateBetween(LocalDate startDate, LocalDate endDate);
}