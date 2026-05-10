package com.focussphere.service;

import com.focussphere.model.Room;
import com.focussphere.model.RoomSessionActivity;
import com.focussphere.model.User;
import com.focussphere.repository.RoomSessionActivityRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomSessionActivityService {

    private static final DateTimeFormatter BREAK_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final RoomSessionActivityRepository roomSessionActivityRepository;

    public RoomSessionActivityService(RoomSessionActivityRepository roomSessionActivityRepository) {
        this.roomSessionActivityRepository = roomSessionActivityRepository;
    }

    @Transactional
    public String startSession(Room room, User user) {
        return startSessionAt(room, user, LocalDateTime.now());
    }

    @Transactional
    public String startSessionAt(Room room, User user, LocalDateTime startAt) {
        if (roomSessionActivityRepository.findFirstByRoomAndUserAndSessionEndIsNullOrderBySessionStartDesc(room, user).isPresent()) {
            return "Session already running.";
        }

        RoomSessionActivity activity = new RoomSessionActivity();
        activity.setRoom(room);
        activity.setUser(user);
        activity.setSessionStart(startAt == null ? LocalDateTime.now() : startAt);
        activity.setTotalBreakSeconds(0L);
        activity.setBreakStartedAt(null);
        activity.setBreakPeriods(null);
        roomSessionActivityRepository.save(activity);
        return "Session started.";
    }

    @Transactional
    public String startBreak(Room room, User user) {
        RoomSessionActivity activity = roomSessionActivityRepository
                .findFirstByRoomAndUserAndSessionEndIsNullOrderBySessionStartDesc(room, user)
                .orElse(null);

        if (activity == null) {
            return "No active session found.";
        }
        if (activity.getBreakStartedAt() != null) {
            return "Break is already active.";
        }

        activity.setBreakStartedAt(LocalDateTime.now());
        roomSessionActivityRepository.save(activity);
        return "Break started.";
    }

    @Transactional
    public String endBreak(Room room, User user) {
        RoomSessionActivity activity = roomSessionActivityRepository
                .findFirstByRoomAndUserAndSessionEndIsNullOrderBySessionStartDesc(room, user)
                .orElse(null);

        if (activity == null) {
            return "No active session found.";
        }
        if (activity.getBreakStartedAt() == null) {
            return "No active break found.";
        }

        closeBreak(activity, LocalDateTime.now());
        roomSessionActivityRepository.save(activity);
        return "Break ended.";
    }

    @Transactional
    public String stopSession(Room room, User user) {
        return stopSessionAt(room, user, LocalDateTime.now());
    }

    @Transactional
    public String stopSessionAt(Room room, User user, LocalDateTime endAt) {
        RoomSessionActivity activity = roomSessionActivityRepository
                .findFirstByRoomAndUserAndSessionEndIsNullOrderBySessionStartDesc(room, user)
                .orElse(null);

        if (activity == null) {
            return "No active session found.";
        }

        LocalDateTime end = endAt == null ? LocalDateTime.now() : endAt;
        if (activity.getBreakStartedAt() != null) {
            closeBreak(activity, end);
        }

        long totalSessionSeconds = Duration.between(activity.getSessionStart(), end).getSeconds();
        long totalBreakSeconds = activity.getTotalBreakSeconds() == null ? 0L : activity.getTotalBreakSeconds();
        long durationSeconds = Math.max(0, totalSessionSeconds - totalBreakSeconds);
        activity.setSessionEnd(end);
        activity.setDurationSeconds(durationSeconds);
        roomSessionActivityRepository.save(activity);
        return "Session stopped.";
    }

    public List<RoomSessionActivity> getHistory(Room room, Integer months, LocalDate specificDate) {
        List<RoomSessionActivity> all = roomSessionActivityRepository.findByRoomOrderBySessionStartDesc(room);

        if (specificDate != null) {
            return all.stream()
                    .filter(item -> item.getSessionStart() != null && item.getSessionStart().toLocalDate().equals(specificDate))
                    .toList();
        }

        if (months != null && months > 0) {
            LocalDateTime cutoff = LocalDateTime.now().minusMonths(months);
            return all.stream()
                    .filter(item -> item.getSessionStart() != null && item.getSessionStart().isAfter(cutoff))
                    .toList();
        }

        return all;
    }

    private void closeBreak(RoomSessionActivity activity, LocalDateTime breakEnd) {
        LocalDateTime breakStart = activity.getBreakStartedAt();
        if (breakStart == null || breakEnd == null || breakEnd.isBefore(breakStart)) {
            activity.setBreakStartedAt(null);
            return;
        }

        long breakSeconds = Math.max(0L, Duration.between(breakStart, breakEnd).getSeconds());
        long updatedTotalBreak = (activity.getTotalBreakSeconds() == null ? 0L : activity.getTotalBreakSeconds()) + breakSeconds;
        activity.setTotalBreakSeconds(updatedTotalBreak);

        String period = breakStart.format(BREAK_TIME_FORMAT) + " - " + breakEnd.format(BREAK_TIME_FORMAT);
        if (activity.getBreakPeriods() == null || activity.getBreakPeriods().isBlank()) {
            activity.setBreakPeriods(period);
        } else {
            activity.setBreakPeriods(activity.getBreakPeriods() + ", " + period);
        }

        activity.setBreakStartedAt(null);
    }
}