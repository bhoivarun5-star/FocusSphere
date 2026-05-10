package com.focussphere.controller;

import com.focussphere.model.User;
import com.focussphere.model.UserFocusSession;
import com.focussphere.model.UserRole;
import com.focussphere.service.FocusStatsService;
import com.focussphere.service.RoomService;
import com.focussphere.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final UserService userService;
    private final FocusStatsService focusStatsService;
    private final RoomService roomService;

    public ProfileController(UserService userService, FocusStatsService focusStatsService, RoomService roomService) {
        this.userService = userService;
        this.focusStatsService = focusStatsService;
        this.roomService = roomService;
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        if (sessionUser.getJoinDate() == null) {
            sessionUser.setJoinDate(LocalDate.now());
        }
        populateProfileModel(model, sessionUser);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String email,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        try {
            User updated = userService.updateProfile(sessionUser.getId(), name, email);
            session.setAttribute("sessionUser", updated);
            model.addAttribute("success", "Profile updated successfully.");
            populateProfileModel(model, updated);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            populateProfileModel(model, sessionUser);
        }
        return "profile";
    }

    private void populateProfileModel(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("currentJoinedRooms", roomService.getCurrentJoinedRooms(user));
        model.addAttribute("roomMembershipHistory", roomService.getMembershipHistoryForProfile(user));
        model.addAttribute("activePage", "profile");
    }

    @GetMapping("/focus-stats")
    public String focusStatsPage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        populateFocusStatsModel(sessionUser, model);
        model.addAttribute("activePage", "stats");
        return "focusStats";
    }

    @PostMapping("/focus-stats/sessions")
    public String addFocusSession(
            @RequestParam String sessionDate,
            @RequestParam Integer durationMinutes,
            @RequestParam(required = false) String notes,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "redirect:/admin/dashboard";
        }

        try {
            focusStatsService.addSession(sessionUser, sessionDate, durationMinutes, notes);
            model.addAttribute("success", "Focus session saved.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        }

        populateFocusStatsModel(sessionUser, model);
        return "focusStats";
    }

    private void populateFocusStatsModel(User user, Model model) {
        List<UserFocusSession> sessions = focusStatsService.getSessions(user);
        int maxDuration = focusStatsService.maxDurationMinutes(user);

        model.addAttribute("user", user);
        model.addAttribute("sessions", sessions);
        model.addAttribute("totalSessions", sessions.size());
        model.addAttribute("totalFocusMinutes", focusStatsService.totalFocusMinutes(user));
        model.addAttribute("averageSessionDuration", focusStatsService.averageDurationMinutes(user));
        model.addAttribute("maxDuration", maxDuration == 0 ? 1 : maxDuration);
    }
}
