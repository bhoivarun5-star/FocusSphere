package com.focussphere.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DTO for Monthly Report Data
 * Contains all aggregated data for a user's monthly report
 */
public class MonthlyReportData implements Serializable {

    private Integer month;
    private Integer year;
    private String monthName;

    // Summary Statistics
    private Integer totalSessions;
    private Integer totalFocusRoomsJoined;
    private Set<String> focusRoomNames;
    private Integer totalBreaks;
    private Long totalSessionSeconds;
    private Double totalFocusMinutes;
    private Double averageSessionMinutes;
    private Long maxSessionSeconds;

    // Session Details
    private List<SessionDetail> sessionDetails;

    // Participation Summary
    private Integer daysWithActivity;
    private Double participationPercentage;
    private String participationTrend;

    // Growth Metrics
    private List<DailyDataPoint> dailyDataPoints;
    private Double consistencyScore; // 0-100
    private String growthTrend;

    public MonthlyReportData() {
        this.sessionDetails = new ArrayList<>();
        this.dailyDataPoints = new ArrayList<>();
    }

    /**
     * Inner class for session details in the table
     */
    public static class SessionDetail {
        private LocalDate sessionDate;
        private String roomName;
        private Long durationSeconds;
        private Integer breakCount;
        private Long breakDurationSeconds;

        public SessionDetail(LocalDate sessionDate, String roomName, Long durationSeconds, 
                           Integer breakCount, Long breakDurationSeconds) {
            this.sessionDate = sessionDate;
            this.roomName = roomName;
            this.durationSeconds = durationSeconds;
            this.breakCount = breakCount;
            this.breakDurationSeconds = breakDurationSeconds;
        }

        // Getters
        public LocalDate getSessionDate() { return sessionDate; }
        public String getRoomName() { return roomName; }
        public Long getDurationSeconds() { return durationSeconds; }
        public Integer getBreakCount() { return breakCount; }
        public Long getBreakDurationSeconds() { return breakDurationSeconds; }

        public String getFormattedDuration() {
            long minutes = durationSeconds / 60;
            long hours = minutes / 60;
            minutes = minutes % 60;
            if (hours > 0) {
                return String.format("%dh %dm", hours, minutes);
            }
            return String.format("%dm", minutes);
        }
    }

    /**
     * Inner class for daily data points in graph
     */
    public static class DailyDataPoint {
        private Integer day;
        private Long sessionSeconds;
        private Integer breakCount;
        private Integer focusQuality; // 0-100 based on consistency

        public DailyDataPoint(Integer day, Long sessionSeconds, Integer breakCount, Integer focusQuality) {
            this.day = day;
            this.sessionSeconds = sessionSeconds;
            this.breakCount = breakCount;
            this.focusQuality = focusQuality;
        }

        // Getters
        public Integer getDay() { return day; }
        public Long getSessionSeconds() { return sessionSeconds; }
        public Integer getBreakCount() { return breakCount; }
        public Integer getFocusQuality() { return focusQuality; }

        public Long getSessionMinutes() { return sessionSeconds / 60; }
    }

    // Getters and Setters
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }

    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }

    public Integer getTotalFocusRoomsJoined() { return totalFocusRoomsJoined; }
    public void setTotalFocusRoomsJoined(Integer totalFocusRoomsJoined) { 
        this.totalFocusRoomsJoined = totalFocusRoomsJoined; 
    }

    public Set<String> getFocusRoomNames() { return focusRoomNames; }
    public void setFocusRoomNames(Set<String> focusRoomNames) { this.focusRoomNames = focusRoomNames; }

    public Integer getTotalBreaks() { return totalBreaks; }
    public void setTotalBreaks(Integer totalBreaks) { this.totalBreaks = totalBreaks; }

    public Long getTotalSessionSeconds() { return totalSessionSeconds; }
    public void setTotalSessionSeconds(Long totalSessionSeconds) { this.totalSessionSeconds = totalSessionSeconds; }

    public Double getTotalFocusMinutes() { return totalFocusMinutes; }
    public void setTotalFocusMinutes(Double totalFocusMinutes) { this.totalFocusMinutes = totalFocusMinutes; }

    public Double getAverageSessionMinutes() { return averageSessionMinutes; }
    public void setAverageSessionMinutes(Double averageSessionMinutes) { this.averageSessionMinutes = averageSessionMinutes; }

    public Long getMaxSessionSeconds() { return maxSessionSeconds; }
    public void setMaxSessionSeconds(Long maxSessionSeconds) { this.maxSessionSeconds = maxSessionSeconds; }

    public List<SessionDetail> getSessionDetails() { return sessionDetails; }
    public void setSessionDetails(List<SessionDetail> sessionDetails) { this.sessionDetails = sessionDetails; }

    public Integer getDaysWithActivity() { return daysWithActivity; }
    public void setDaysWithActivity(Integer daysWithActivity) { this.daysWithActivity = daysWithActivity; }

    public Double getParticipationPercentage() { return participationPercentage; }
    public void setParticipationPercentage(Double participationPercentage) { 
        this.participationPercentage = participationPercentage; 
    }

    public String getParticipationTrend() { return participationTrend; }
    public void setParticipationTrend(String participationTrend) { this.participationTrend = participationTrend; }

    public List<DailyDataPoint> getDailyDataPoints() { return dailyDataPoints; }
    public void setDailyDataPoints(List<DailyDataPoint> dailyDataPoints) { this.dailyDataPoints = dailyDataPoints; }

    public Double getConsistencyScore() { return consistencyScore; }
    public void setConsistencyScore(Double consistencyScore) { this.consistencyScore = consistencyScore; }

    public String getGrowthTrend() { return growthTrend; }
    public void setGrowthTrend(String growthTrend) { this.growthTrend = growthTrend; }

    public String getFormattedTotalMinutes() {
        long minutes = (totalSessionSeconds != null) ? totalSessionSeconds / 60 : 0;
        long hours = minutes / 60;
        minutes = minutes % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        }
        return String.format("%dm", minutes);
    }

    public boolean hasData() {
        return totalSessions != null && totalSessions > 0;
    }
}
