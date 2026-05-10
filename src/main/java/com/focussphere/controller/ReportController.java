package com.focussphere.controller;

import com.focussphere.dto.MonthlyReportData;
import com.focussphere.model.Report;
import com.focussphere.model.User;
import com.focussphere.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.YearMonth;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Get current month and year
        YearMonth now = YearMonth.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        model.addAttribute("activePage", "reports");
        model.addAttribute("selectedMonth", currentMonth);
        model.addAttribute("selectedYear", currentYear);
        model.addAttribute("monthNames", new java.util.HashMap<>() {{
            put(1, "January");
            put(2, "February");
            put(3, "March");
            put(4, "April");
            put(5, "May");
            put(6, "June");
            put(7, "July");
            put(8, "August");
            put(9, "September");
            put(10, "October");
            put(11, "November");
            put(12, "December");
        }});

        return "reports";
    }

    @GetMapping("/api/reports/generate")
    @ResponseBody
    public String generateReport(
            @RequestParam Integer month,
            @RequestParam Integer year,
            HttpSession session) {
        
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "{\"success\": false, \"message\": \"Not authenticated\"}";
        }

        try {
            if (month == null || month < 1 || month > 12) {
                return "{\"success\": false, \"message\": \"Invalid month\"}";
            }
            if (year == null || year < 2000 || year > 2100) {
                return "{\"success\": false, \"message\": \"Invalid year\"}";
            }

            Report report = reportService.generateReport(sessionUser, month, year);
            return "{\"success\": true, \"reportId\": " + report.getId() + ", \"message\": \"Report generated\"}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/api/reports/{reportId}")
    @ResponseBody
    public Report getReport(@PathVariable Long reportId, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return null;
        }

        Report report = reportService.getReport(reportId);
        if (report == null || !report.getUser().getId().equals(sessionUser.getId())) {
            return null;
        }

        return report;
    }

    @GetMapping("/api/reports/current")
    @ResponseBody
    public Report getCurrentReport(
            @RequestParam Integer month,
            @RequestParam Integer year,
            HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return null;
        }

        try {
            return reportService.generateReport(sessionUser, month, year);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get comprehensive monthly report data as JSON
     */
    @GetMapping("/api/reports/monthly-data")
    @ResponseBody
    public ResponseEntity<MonthlyReportData> getMonthlyReportData(
            @RequestParam Integer month,
            @RequestParam Integer year,
            HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(null);
        }

        try {
            if (month == null || month < 1 || month > 12) {
                return ResponseEntity.badRequest().body(null);
            }
            if (year == null || year < 2000 || year > 2100) {
                return ResponseEntity.badRequest().body(null);
            }

            MonthlyReportData reportData = reportService.getMonthlyReportData(sessionUser, month, year);
            return ResponseEntity.ok(reportData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
