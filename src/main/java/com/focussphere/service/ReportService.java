package com.focussphere.service;

import com.focussphere.model.Report;
import com.focussphere.model.RoomSessionActivity;
import com.focussphere.model.User;
import com.focussphere.model.UserFocusSession;
import com.focussphere.repository.ReportRepository;
import com.focussphere.repository.RoomSessionActivityRepository;
import com.focussphere.repository.UserFocusSessionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserFocusSessionRepository userFocusSessionRepository;
    private final RoomSessionActivityRepository roomSessionActivityRepository;
    private final FocusStatsService focusStatsService;

    public ReportService(
            ReportRepository reportRepository,
            UserFocusSessionRepository userFocusSessionRepository,
            RoomSessionActivityRepository roomSessionActivityRepository,
            FocusStatsService focusStatsService) {
        this.reportRepository = reportRepository;
        this.userFocusSessionRepository = userFocusSessionRepository;
        this.roomSessionActivityRepository = roomSessionActivityRepository;
        this.focusStatsService = focusStatsService;
    }

    public Report generateReport(User user, Integer month, Integer year) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be valid");
        }

        // Check if report already exists
        Report existingReport = reportRepository.findByUserAndReportMonthAndReportYear(user, month, year)
                .orElse(null);
        if (existingReport != null) {
            return existingReport;
        }

        Report report = new Report(user, month, year);

        // Get all sessions for the user in the specified month
        List<UserFocusSession> monthlySessions = getUserSessionsForMonth(user, month, year);
        report.setTotalSessions(monthlySessions.size());
        
        if (!monthlySessions.isEmpty()) {
            int totalMinutes = monthlySessions.stream()
                    .map(UserFocusSession::getDurationMinutes)
                    .filter(d -> d != null)
                    .mapToInt(Integer::intValue)
                    .sum();
            report.setTotalFocusMinutes(totalMinutes);

            double avgMinutes = (double) totalMinutes / monthlySessions.size();
            report.setAverageDurationMinutes(Math.round(avgMinutes * 10.0) / 10.0);

            int maxMinutes = monthlySessions.stream()
                    .map(UserFocusSession::getDurationMinutes)
                    .filter(d -> d != null)
                    .max(Integer::compareTo)
                    .orElse(0);
            report.setMaxDurationMinutes(maxMinutes);
        }

        // Get room session activities for the month
        List<RoomSessionActivity> roomActivities = getUserRoomActivitiesForMonth(user, month, year);
        long totalActivitySeconds = roomActivities.stream()
                .map(RoomSessionActivity::getDurationSeconds)
                .filter(d -> d != null)
                .mapToLong(Long::longValue)
                .sum();
        report.setTotalRoomActivitySeconds(totalActivitySeconds);

        // Build session details with room information
        String sessionDetails = buildSessionDetails(monthlySessions, roomActivities);
        report.setSessionDetails(sessionDetails);

        return reportRepository.save(report);
    }

    public List<Report> getUserReports(User user) {
        if (user == null) {
            return List.of();
        }
        return reportRepository.findByUserOrderByReportYearDescReportMonthDesc(user);
    }

    public Report getReport(Long reportId) {
        return reportRepository.findById(reportId).orElse(null);
    }

    private List<UserFocusSession> getUserSessionsForMonth(User user, Integer month, Integer year) {
        List<UserFocusSession> allSessions = userFocusSessionRepository.findByUserOrderBySessionDateDescIdDesc(user);
        YearMonth targetMonth = YearMonth.of(year, month);

        return allSessions.stream()
                .filter(session -> {
                    YearMonth sessionYearMonth = YearMonth.from(session.getSessionDate());
                    return sessionYearMonth.equals(targetMonth);
                })
                .sorted(Comparator.comparing(UserFocusSession::getSessionDate).reversed())
                .collect(Collectors.toList());
    }

    private List<RoomSessionActivity> getUserRoomActivitiesForMonth(User user, Integer month, Integer year) {
        List<RoomSessionActivity> allActivities = roomSessionActivityRepository.findAll();
        YearMonth targetMonth = YearMonth.of(year, month);

        return allActivities.stream()
                .filter(activity -> activity.getUser().getId().equals(user.getId()) &&
                        activity.getSessionStart() != null &&
                        YearMonth.from(activity.getSessionStart()).equals(targetMonth))
                .collect(Collectors.toList());
    }

    private String buildSessionDetails(List<UserFocusSession> focusSessions, List<RoomSessionActivity> roomActivities) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Focus Sessions Summary:\n");
        if (focusSessions.isEmpty()) {
            sb.append("No focus sessions recorded for this month.\n");
        } else {
            for (UserFocusSession session : focusSessions) {
                sb.append(String.format("• %s: %d minutes", 
                    session.getSessionDate(), 
                    session.getDurationMinutes()));
                if (session.getNotes() != null && !session.getNotes().isEmpty()) {
                    sb.append(" - ").append(session.getNotes());
                }
                sb.append("\n");
            }
        }

        sb.append("\nRoom Activity Details:\n");
        if (roomActivities.isEmpty()) {
            sb.append("No room activities recorded for this month.\n");
        } else {
            // Group by room
            Map<String, Long> roomActivityMap = roomActivities.stream()
                    .collect(Collectors.groupingBy(
                        activity -> activity.getRoom().getRoomName(),
                        Collectors.summingLong(activity -> 
                            activity.getDurationSeconds() != null ? activity.getDurationSeconds() : 0
                        )
                    ));

            roomActivityMap.forEach((roomName, seconds) -> {
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long remainingMinutes = minutes % 60;
                sb.append(String.format("• %s: %dh %dm\n", roomName, hours, remainingMinutes));
            });
        }

        return sb.toString();
    }
}
