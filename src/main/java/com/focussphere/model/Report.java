package com.focussphere.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "report_month", nullable = false)
    private Integer reportMonth; // 1-12

    @Column(name = "report_year", nullable = false)
    private Integer reportYear; // e.g., 2026

    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions = 0;

    @Column(name = "total_focus_minutes", nullable = false)
    private Integer totalFocusMinutes = 0;

    @Column(name = "average_duration_minutes")
    private Double averageDurationMinutes = 0.0;

    @Column(name = "max_duration_minutes")
    private Integer maxDurationMinutes = 0;

    @Column(name = "total_room_activity_minutes")
    private Long totalRoomActivitySeconds = 0L;

    @Column(length = 3000)
    private String sessionDetails; // JSON or descriptive text with room info

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    public Report() {
    }

    public Report(User user, Integer reportMonth, Integer reportYear) {
        this.user = user;
        this.reportMonth = reportMonth;
        this.reportYear = reportYear;
        this.generatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(Integer reportMonth) {
        this.reportMonth = reportMonth;
    }

    public Integer getReportYear() {
        return reportYear;
    }

    public void setReportYear(Integer reportYear) {
        this.reportYear = reportYear;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getTotalFocusMinutes() {
        return totalFocusMinutes;
    }

    public void setTotalFocusMinutes(Integer totalFocusMinutes) {
        this.totalFocusMinutes = totalFocusMinutes;
    }

    public Double getAverageDurationMinutes() {
        return averageDurationMinutes;
    }

    public void setAverageDurationMinutes(Double averageDurationMinutes) {
        this.averageDurationMinutes = averageDurationMinutes;
    }

    public Integer getMaxDurationMinutes() {
        return maxDurationMinutes;
    }

    public void setMaxDurationMinutes(Integer maxDurationMinutes) {
        this.maxDurationMinutes = maxDurationMinutes;
    }

    public Long getTotalRoomActivitySeconds() {
        return totalRoomActivitySeconds;
    }

    public void setTotalRoomActivitySeconds(Long totalRoomActivitySeconds) {
        this.totalRoomActivitySeconds = totalRoomActivitySeconds;
    }

    public String getSessionDetails() {
        return sessionDetails;
    }

    public void setSessionDetails(String sessionDetails) {
        this.sessionDetails = sessionDetails;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
