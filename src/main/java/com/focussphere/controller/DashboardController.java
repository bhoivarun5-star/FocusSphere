package com.focussphere.controller;

import com.focussphere.model.User;
import com.focussphere.model.UserRole;
import com.focussphere.service.AdminInsightsService;
import com.focussphere.service.NotificationService;
import com.focussphere.service.ReportService;
import com.focussphere.service.RoomService;
import com.focussphere.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.YearMonth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final RoomService roomService;
    private final UserService userService;
    private final AdminInsightsService adminInsightsService;
    private final NotificationService notificationService;
    private final ReportService reportService;

    public DashboardController(
            RoomService roomService,
            UserService userService,
            AdminInsightsService adminInsightsService,
            NotificationService notificationService,
            ReportService reportService) {
        this.roomService = roomService;
        this.userService = userService;
        this.adminInsightsService = adminInsightsService;
        this.notificationService = notificationService;
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public String userDashboard(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "name-asc") String sortBy,
            @RequestParam(defaultValue = "all") String visibilityFilter,
            @RequestParam(defaultValue = "") String search
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("isAdmin", sessionUser.getRole() == UserRole.ADMIN);
        model.addAttribute("roomDiscoveryRows", roomService.getRoomDiscoveryRows(sessionUser, sortBy, visibilityFilter, search));
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("visibilityFilter", visibilityFilter);
        model.addAttribute("search", search);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model,
            @RequestParam(defaultValue = "name-asc") String sortBy,
            @RequestParam(defaultValue = "all") String visibilityFilter,
            @RequestParam(defaultValue = "") String search
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        return userDashboard(session, model, sortBy, visibilityFilter, search);
    }

    @GetMapping("/admin/users")
    public String adminUsers(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        populateAdminDashboard(model, sessionUser, "users");
        return "adminDashboard";
    }

    @GetMapping("/admin/rooms")
    public String adminRooms(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        populateAdminDashboard(model, sessionUser, "rooms");
        return "adminDashboard";
    }

    @GetMapping("/admin/analytics")
    public String adminAnalytics(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        populateAdminDashboard(model, sessionUser, "analytics");
        return "adminDashboard";
    }

    @GetMapping("/admin/system-report")
    public String adminSystemReport(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() != UserRole.ADMIN) {
            return "redirect:/dashboard";
        }

        populateAdminDashboard(model, sessionUser, "system-report");
        return "adminDashboard";
    }

    @GetMapping("/rooms/created")
    public String createdRooms(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("createdRooms", roomService.getRoomsCreatedBy(sessionUser));
        model.addAttribute("activePage", "created");
        return "createdRooms";
    }

    @GetMapping("/requests/pending")
    public String pendingRequests(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("pendingRequestsForMyRooms", roomService.getPendingJoinRequestsForCreator(sessionUser));
        model.addAttribute("activePage", "requests");
        return "pendingRequests";
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("user", sessionUser);
        model.addAttribute("notifications", notificationService.getNotificationsForUser(sessionUser));
        model.addAttribute("unreadCount", notificationService.getUnreadNotificationCount(sessionUser));
        model.addAttribute("activePage", "notifications");
        return "notifications";
    }

    @GetMapping("/my-reports")
    public String reports(
            HttpSession session,
            Model model,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        // Default to current month if not specified
        if (month == null || year == null) {
            YearMonth now = YearMonth.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        // Validate month and year
        if (month < 1 || month > 12 || year < 2000 || year > 2100) {
            YearMonth now = YearMonth.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        model.addAttribute("user", sessionUser);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("monthNames", getMonthNames());
        model.addAttribute("activePage", "reports");
        return "reports";
    }

    private java.util.Map<Integer, String> getMonthNames() {
        java.util.Map<Integer, String> months = new java.util.LinkedHashMap<>();
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (int i = 1; i <= 12; i++) {
            months.put(i, monthNames[i - 1]);
        }
        return months;
    }

    private void populateAdminDashboard(Model model, User sessionUser, String activeSection) {
        model.addAttribute("admin", sessionUser);
        model.addAttribute("availableRooms", roomService.getAvailableRoomsForUser(sessionUser));
        model.addAttribute("roomMonitorRows", roomService.getRoomMonitorRows());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("analytics", adminInsightsService.getAnalytics());
        model.addAttribute("systemReport", adminInsightsService.getSystemReport());
        model.addAttribute("activeSection", activeSection);
    }
}
