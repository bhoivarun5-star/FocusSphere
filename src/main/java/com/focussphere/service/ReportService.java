package com.focussphere.service;

import com.focussphere.dto.MonthlyReportData;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * Generate comprehensive monthly report data for dashboard display
     */
    public MonthlyReportData getMonthlyReportData(User user, Integer month, Integer year) {
        if (user == null || month == null || year == null) {
            return new MonthlyReportData();
        }

        MonthlyReportData reportData = new MonthlyReportData();
        reportData.setMonth(month);
        reportData.setYear(year);
        reportData.setMonthName(getMonthName(month));

        // Get all activities for the month
        List<RoomSessionActivity> monthlyActivities = getUserRoomActivitiesForMonth(user, month, year);
        List<UserFocusSession> monthlySessions = getUserSessionsForMonth(user, month, year);

        if (monthlyActivities.isEmpty() && monthlySessions.isEmpty()) {
            return reportData;
        }

        // Calculate summary statistics from room activities
        calculateSummaryStats(reportData, monthlyActivities, monthlySessions);

        // Build session details table
        buildSessionDetailsTable(reportData, monthlyActivities);

        // Calculate participation metrics
        calculateParticipationMetrics(reportData, monthlyActivities, month, year);

        // Generate daily data points for graph
        generateDailyDataPoints(reportData, monthlyActivities, month, year);

        return reportData;
    }

    private void calculateSummaryStats(MonthlyReportData reportData, 
                                      List<RoomSessionActivity> roomActivities,
                                      List<UserFocusSession> focusSessions) {
        if (roomActivities.isEmpty()) {
            reportData.setTotalSessions(focusSessions.size());
            reportData.setTotalFocusRoomsJoined(0);
            reportData.setFocusRoomNames(new HashSet<>());
            reportData.setTotalBreaks(0);
            reportData.setTotalSessionSeconds(0L);
            reportData.setTotalFocusMinutes(0.0);
            reportData.setAverageSessionMinutes(0.0);
            reportData.setMaxSessionSeconds(0L);
            return;
        }

        // Total session count
        reportData.setTotalSessions(roomActivities.size());

        // Unique rooms joined
        Set<String> roomNames = roomActivities.stream()
                .map(activity -> activity.getRoom().getRoomName())
                .collect(Collectors.toSet());
        reportData.setTotalFocusRoomsJoined(roomNames.size());
        reportData.setFocusRoomNames(roomNames);

        // Total session seconds and calculate breaks
        long totalSeconds = 0;
        int totalBreaks = 0;
        long maxSeconds = 0;

        for (RoomSessionActivity activity : roomActivities) {
            if (activity.getDurationSeconds() != null) {
                totalSeconds += activity.getDurationSeconds();
                maxSeconds = Math.max(maxSeconds, activity.getDurationSeconds());
            }
            if (activity.getTotalBreakSeconds() != null) {
                totalBreaks += (int) (activity.getTotalBreakSeconds() / 60); // Convert to count
            }
        }

        reportData.setTotalSessionSeconds(totalSeconds);
        reportData.setTotalFocusMinutes((double) totalSeconds / 60);
        reportData.setMaxSessionSeconds(maxSeconds);
        reportData.setTotalBreaks(Math.max(totalBreaks, (int) roomActivities.stream()
                .filter(a -> a.getTotalBreakSeconds() != null && a.getTotalBreakSeconds() > 0)
                .count()));
        
        // Calculate average
        double avgSeconds = (double) totalSeconds / roomActivities.size();
        reportData.setAverageSessionMinutes(avgSeconds / 60);
    }

    private void buildSessionDetailsTable(MonthlyReportData reportData, 
                                         List<RoomSessionActivity> roomActivities) {
        List<MonthlyReportData.SessionDetail> sessionDetails = new ArrayList<>();

        for (RoomSessionActivity activity : roomActivities) {
            LocalDate sessionDate = activity.getSessionStart() != null 
                    ? activity.getSessionStart().toLocalDate() 
                    : null;
            String roomName = activity.getRoom() != null 
                    ? activity.getRoom().getRoomName() 
                    : "Unknown Room";
            Long duration = activity.getDurationSeconds() != null 
                    ? activity.getDurationSeconds() 
                    : 0L;
            Integer breakCount = activity.getTotalBreakSeconds() != null && activity.getTotalBreakSeconds() > 0
                    ? 1
                    : 0;
            Long breakDuration = activity.getTotalBreakSeconds() != null 
                    ? activity.getTotalBreakSeconds() 
                    : 0L;

            sessionDetails.add(new MonthlyReportData.SessionDetail(
                    sessionDate, roomName, duration, breakCount, breakDuration
            ));
        }

        // Sort by date descending
        sessionDetails.sort((a, b) -> {
            if (a.getSessionDate() == null || b.getSessionDate() == null) return 0;
            return b.getSessionDate().compareTo(a.getSessionDate());
        });

        reportData.setSessionDetails(sessionDetails);
    }

    private void calculateParticipationMetrics(MonthlyReportData reportData,
                                               List<RoomSessionActivity> roomActivities,
                                               Integer month, Integer year) {
        if (roomActivities.isEmpty()) {
            reportData.setDaysWithActivity(0);
            reportData.setParticipationPercentage(0.0);
            reportData.setParticipationTrend("No activity");
            reportData.setConsistencyScore(0.0);
            return;
        }

        // Calculate days with activity
        Set<LocalDate> activeDays = roomActivities.stream()
                .filter(a -> a.getSessionStart() != null)
                .map(a -> a.getSessionStart().toLocalDate())
                .collect(Collectors.toSet());
        
        int daysWithActivity = activeDays.size();
        reportData.setDaysWithActivity(daysWithActivity);

        // Calculate participation percentage
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        double participationPercentage = (double) daysWithActivity / daysInMonth * 100;
        reportData.setParticipationPercentage(Math.round(participationPercentage * 10.0) / 10.0);

        // Determine participation trend
        String trend = determineTrend(participationPercentage);
        reportData.setParticipationTrend(trend);

        // Calculate consistency score (0-100)
        double consistencyScore = calculateConsistencyScore(roomActivities, daysWithActivity);
        reportData.setConsistencyScore(Math.round(consistencyScore * 10.0) / 10.0);
    }

    private String determineTrend(double percentage) {
        if (percentage >= 80) return "Excellent - Very Consistent";
        if (percentage >= 60) return "Good - Regularly Active";
        if (percentage >= 40) return "Moderate - Fairly Consistent";
        if (percentage >= 20) return "Fair - Occasional Activity";
        return "Low - Sparse Activity";
    }

    private double calculateConsistencyScore(List<RoomSessionActivity> roomActivities, int daysWithActivity) {
        if (roomActivities.isEmpty() || daysWithActivity == 0) {
            return 0.0;
        }

        // Calculate average duration variance
        double avgDuration = roomActivities.stream()
                .mapToLong(a -> a.getDurationSeconds() != null ? a.getDurationSeconds() : 0)
                .average()
                .orElse(0);

        if (avgDuration == 0) return 0.0;

        double variance = roomActivities.stream()
                .mapToDouble(a -> {
                    long duration = a.getDurationSeconds() != null ? a.getDurationSeconds() : 0;
                    return Math.pow(duration - avgDuration, 2);
                })
                .average()
                .orElse(0);

        double stdDev = Math.sqrt(variance);
        double coefficientOfVariation = (stdDev / avgDuration) * 100;

        // Lower variance = higher consistency
        double consistencyFromVariance = Math.max(0, 100 - coefficientOfVariation);

        // Combine with activity frequency
        double consistencyFromFrequency = (daysWithActivity / 31.0) * 100; // Assume max 31 days

        return (consistencyFromVariance * 0.6 + consistencyFromFrequency * 0.4);
    }

    private void generateDailyDataPoints(MonthlyReportData reportData,
                                        List<RoomSessionActivity> roomActivities,
                                        Integer month, Integer year) {
        Map<Integer, Long> dailySeconds = new LinkedHashMap<>();
        Map<Integer, Integer> dailyBreaks = new LinkedHashMap<>();
        
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        // Initialize all days
        for (int day = 1; day <= daysInMonth; day++) {
            dailySeconds.put(day, 0L);
            dailyBreaks.put(day, 0);
        }

        // Aggregate by day
        for (RoomSessionActivity activity : roomActivities) {
            if (activity.getSessionStart() != null) {
                int day = activity.getSessionStart().toLocalDate().getDayOfMonth();
                long currentSeconds = dailySeconds.getOrDefault(day, 0L);
                long duration = activity.getDurationSeconds() != null ? activity.getDurationSeconds() : 0;
                dailySeconds.put(day, currentSeconds + duration);

                if (activity.getTotalBreakSeconds() != null && activity.getTotalBreakSeconds() > 0) {
                    dailyBreaks.put(day, dailyBreaks.getOrDefault(day, 0) + 1);
                }
            }
        }

        // Generate data points with focus quality
        List<MonthlyReportData.DailyDataPoint> dataPoints = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            long seconds = dailySeconds.get(day);
            int breaks = dailyBreaks.get(day);
            
            // Focus quality: based on session duration and consistency
            int focusQuality = calculateDailyFocusQuality(seconds, breaks);
            
            dataPoints.add(new MonthlyReportData.DailyDataPoint(day, seconds, breaks, focusQuality));
        }

        reportData.setDailyDataPoints(dataPoints);

        // Determine growth trend
        String growthTrend = calculateGrowthTrend(dailySeconds, daysInMonth);
        reportData.setGrowthTrend(growthTrend);
    }

    private int calculateDailyFocusQuality(long sessionSeconds, int breaks) {
        // Base quality from session duration (0-100)
        long sessionMinutes = sessionSeconds / 60;
        int qualityFromDuration = (int) Math.min(100, (sessionMinutes / 120.0) * 100); // 2 hours = 100%

        // Adjust for breaks (fewer is better)
        int breakPenalty = Math.min(20, breaks * 5);

        return Math.max(0, qualityFromDuration - breakPenalty);
    }

    private String calculateGrowthTrend(Map<Integer, Long> dailySeconds, int daysInMonth) {
        if (daysInMonth < 2) return "Insufficient data";

        // Split month into two halves
        int midPoint = daysInMonth / 2;
        long firstHalfTotal = 0;
        long secondHalfTotal = 0;

        for (int day = 1; day <= midPoint; day++) {
            firstHalfTotal += dailySeconds.getOrDefault(day, 0L);
        }
        for (int day = midPoint + 1; day <= daysInMonth; day++) {
            secondHalfTotal += dailySeconds.getOrDefault(day, 0L);
        }

        if (firstHalfTotal == 0 && secondHalfTotal == 0) return "No activity";

        double growthPercentage = firstHalfTotal == 0 
                ? 100 
                : ((secondHalfTotal - firstHalfTotal) / (double) firstHalfTotal) * 100;

        if (growthPercentage > 20) return "📈 Strong Growth";
        if (growthPercentage > 0) return "↗️ Slight Growth";
        if (growthPercentage == 0) return "→ Steady";
        if (growthPercentage > -20) return "↘️ Slight Decline";
        return "📉 Declining";
    }

    private String getMonthName(Integer month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        return month >= 1 && month <= 12 ? months[month] : "Unknown";
    }
}
