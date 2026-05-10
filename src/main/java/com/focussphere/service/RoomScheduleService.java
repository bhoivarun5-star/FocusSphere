package com.focussphere.service;

import com.focussphere.model.Room;
import com.focussphere.model.RoomSchedule;
import com.focussphere.model.User;
import com.focussphere.repository.RoomScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoomScheduleService {

    private final RoomScheduleRepository roomScheduleRepository;

    public RoomScheduleService(RoomScheduleRepository roomScheduleRepository) {
        this.roomScheduleRepository = roomScheduleRepository;
    }

    public void createSchedule(Room room, User creator, LocalDate date, LocalTime time, Integer durationMinutes) {
        if (date == null || time == null) {
            throw new IllegalArgumentException("Schedule date and time are required.");
        }
        if (durationMinutes == null || durationMinutes < 5 || durationMinutes > 600) {
            throw new IllegalArgumentException("Duration must be between 5 and 600 minutes.");
        }

        RoomSchedule schedule = new RoomSchedule();
        schedule.setRoom(room);
        schedule.setCreatedBy(creator);
        schedule.setScheduleDate(date);
        schedule.setScheduleTime(time);
        schedule.setDurationMinutes(durationMinutes);
        schedule.setCreatedAt(LocalDateTime.now());
        roomScheduleRepository.save(schedule);
    }

    public List<RoomSchedule> getSchedules(Room room) {
        return roomScheduleRepository.findByRoomOrderByScheduleDateDescScheduleTimeDesc(room);
    }

    public void deleteSchedule(Room room, User creator, Long scheduleId) {
        RoomSchedule schedule = roomScheduleRepository.findByIdAndRoomAndCreatedBy(scheduleId, room, creator)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found or you do not have permission to delete it."));
        roomScheduleRepository.delete(schedule);
    }
}