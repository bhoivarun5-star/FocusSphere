package com.focussphere.service;

import com.focussphere.model.User;
import com.focussphere.model.UserFocusSession;
import com.focussphere.repository.UserFocusSessionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FocusStatsService {

    private final UserFocusSessionRepository userFocusSessionRepository;

    public FocusStatsService(UserFocusSessionRepository userFocusSessionRepository) {
        this.userFocusSessionRepository = userFocusSessionRepository;
    }

    public UserFocusSession addSession(User user, String sessionDate, Integer durationMinutes, String notes) {
        if (user == null) {
            throw new IllegalArgumentException("User session is invalid.");
        }
        if (sessionDate == null || sessionDate.isBlank()) {
            throw new IllegalArgumentException("Session date is required.");
        }
        if (durationMinutes == null || durationMinutes < 1 || durationMinutes > 1440) {
            throw new IllegalArgumentException("Duration must be between 1 and 1440 minutes.");
        }

        UserFocusSession session = new UserFocusSession();
        session.setUser(user);
        session.setSessionDate(LocalDate.parse(sessionDate.trim()));
        session.setDurationMinutes(durationMinutes);

        String cleanedNotes = notes == null ? null : notes.trim();
        if (cleanedNotes != null && cleanedNotes.length() > 600) {
            throw new IllegalArgumentException("Notes can be up to 600 characters.");
        }
        session.setNotes(cleanedNotes == null || cleanedNotes.isBlank() ? null : cleanedNotes);
        return userFocusSessionRepository.save(session);
    }

    public List<UserFocusSession> getSessions(User user) {
        if (user == null) {
            return List.of();
        }
        return userFocusSessionRepository.findByUserOrderBySessionDateDescIdDesc(user);
    }

    public int totalSessions(User user) {
        return getSessions(user).size();
    }

    public int totalFocusMinutes(User user) {
        return getSessions(user).stream()
                .map(UserFocusSession::getDurationMinutes)
                .filter(v -> v != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public double averageDurationMinutes(User user) {
        List<UserFocusSession> sessions = getSessions(user);
        if (sessions.isEmpty()) {
            return 0;
        }
        return Math.round((totalFocusMinutes(user) * 1.0 / sessions.size()) * 10.0) / 10.0;
    }

    public int maxDurationMinutes(User user) {
        return getSessions(user).stream()
                .map(UserFocusSession::getDurationMinutes)
                .filter(v -> v != null)
                .max(Integer::compareTo)
                .orElse(0);
    }
}
