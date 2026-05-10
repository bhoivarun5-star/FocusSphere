package com.focussphere.controller;

import com.focussphere.model.Room;
import com.focussphere.model.RoomVisibility;
import com.focussphere.model.User;
import com.focussphere.model.UserRole;
import com.focussphere.service.MessageService;
import com.focussphere.service.RoomService;
import com.focussphere.service.RoomSessionActivityService;
import com.focussphere.service.RoomScheduleService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RoomController {

    private final RoomService roomService;
    private final MessageService messageService;
    private final RoomSessionActivityService roomSessionActivityService;
    private final RoomScheduleService roomScheduleService;

    public RoomController(
            RoomService roomService,
            MessageService messageService,
            RoomSessionActivityService roomSessionActivityService,
            RoomScheduleService roomScheduleService) {
        this.roomService = roomService;
        this.messageService = messageService;
        this.roomSessionActivityService = roomSessionActivityService;
        this.roomScheduleService = roomScheduleService;
    }

    @GetMapping("/rooms/create")
    public String createRoomPage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot create rooms.");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("activePage", "create");
        return "createRoom";
    }

    @PostMapping("/rooms/create")
    public String createRoom(
            @RequestParam String roomName,
            @RequestParam(required = false) String roomDescription,
            @RequestParam(defaultValue = "PUBLIC") String visibility,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot create rooms.");
            return "redirect:/admin/dashboard";
        }

        if (roomName == null || roomName.trim().isEmpty()) {
            model.addAttribute("error", "Room name is required.");
                        model.addAttribute("activePage", "create");
            return "createRoom";
        }

        RoomVisibility roomVisibility;
        try {
            roomVisibility = RoomVisibility.valueOf(visibility.trim().toUpperCase());
        } catch (Exception ex) {
            model.addAttribute("error", "Please select a valid room visibility.");
                        model.addAttribute("activePage", "create");
            return "createRoom";
        }

        String description = roomDescription == null ? null : roomDescription.trim();
        if (description != null && description.length() > 400) {
            model.addAttribute("error", "Room description must be 400 characters or less.");
                        model.addAttribute("activePage", "create");
            return "createRoom";
        }

        Room room = roomService.createRoom(roomName.trim(), description, roomVisibility, sessionUser);
        session.setAttribute("flashRoomMessage", "Room created successfully.");
        return "redirect:/rooms/" + room.getRoomCode();
    }

    @GetMapping("/rooms/join")
    public String joinRoomPage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot join rooms.");
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("rooms", roomService.getPublicRoomsCreatedByOthers(sessionUser));
        model.addAttribute("activePage", "join");
        return "joinRoom";
    }

    @PostMapping("/rooms/join")
        public String joinRoom(
            @RequestParam String roomCode,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot join rooms.");
            return "redirect:/admin/dashboard";
        }

        try {
            String message = roomService.requestToJoinRoom(roomCode.trim().toUpperCase(), sessionUser);
            model.addAttribute("success", message);
            model.addAttribute("rooms", roomService.getPublicRoomsCreatedByOthers(sessionUser));
            return "joinRoom";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("rooms", roomService.getPublicRoomsCreatedByOthers(sessionUser));
            return "joinRoom";
        }
    }

    @PostMapping("/rooms/{roomCode}/request-join")
    public String requestJoinFromList(
            @PathVariable String roomCode,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot join rooms.");
            return "redirect:/admin/dashboard";
        }

        try {
            String message = roomService.requestToJoinRoom(roomCode.trim().toUpperCase(), sessionUser);
            model.addAttribute("success", message);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        }

        model.addAttribute("rooms", roomService.getPublicRoomsCreatedByOthers(sessionUser));
        return "joinRoom";
    }

    @PostMapping("/rooms/requests/{requestId}/approve")
    public String approveRequest(
            @PathVariable Long requestId,
            HttpSession session
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            roomService.approveJoinRequest(requestId, sessionUser);
        } catch (IllegalArgumentException ignored) {
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/rooms/requests/{requestId}/reject")
    public String rejectRequest(
            @PathVariable Long requestId,
            HttpSession session
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            roomService.rejectJoinRequest(requestId, sessionUser);
        } catch (IllegalArgumentException ignored) {
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/rooms/{roomCode}")
    public String roomPage(
            @PathVariable String roomCode,
            HttpSession session,
            Model model
    ) {
        return loadRoomViewModel(roomCode, null, null, session, model, "room");
    }

    @GetMapping("/rooms/{roomCode}/activity")
    public String roomActivityPage(
            @PathVariable String roomCode,
            @RequestParam(required = false) Integer historyMonths,
            @RequestParam(required = false) String historyDate,
            HttpSession session,
            Model model
    ) {
        return loadRoomViewModel(roomCode, historyMonths, historyDate, session, model, "roomActivity");
    }

    private String loadRoomViewModel(
            String roomCode,
            Integer historyMonths,
            String historyDate,
            HttpSession session,
            Model model,
            String viewName
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null) {
            model.addAttribute("error", "Room does not exist.");
            model.addAttribute("rooms", roomService.getPublicRoomsCreatedByOthers(sessionUser));
            return "joinRoom";
        }

        if (sessionUser.getRole() != UserRole.ADMIN && !roomService.isUserApprovedForRoom(room, sessionUser)) {
            return "redirect:/rooms/join";
        }

        if (sessionUser.getRole() != UserRole.ADMIN && !roomService.isUserMember(room, sessionUser)) {
            roomService.joinRoom(room.getRoomCode(), sessionUser);
        }

        Object flashRoomMessage = session.getAttribute("flashRoomMessage");
        if (flashRoomMessage != null) {
            model.addAttribute("success", flashRoomMessage.toString());
            session.removeAttribute("flashRoomMessage");
        }
        Object flashRoomError = session.getAttribute("flashRoomError");
        if (flashRoomError != null) {
            model.addAttribute("error", flashRoomError.toString());
            session.removeAttribute("flashRoomError");
        }

        model.addAttribute("room", room);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("isCreator", roomService.isRoomCreator(room, sessionUser));
        model.addAttribute("isAdminReadOnly", sessionUser.getRole() == UserRole.ADMIN);
        model.addAttribute("members", roomService.getRoomMembers(room));
        model.addAttribute("messages", messageService.getMessagesByRoom(room));
        model.addAttribute("schedules", roomScheduleService.getSchedules(room));

        LocalDate filterDate = null;
        if (historyDate != null && !historyDate.isBlank()) {
            try {
                filterDate = LocalDate.parse(historyDate.trim());
            } catch (Exception ex) {
                model.addAttribute("error", "Invalid history date format.");
            }
        }
        model.addAttribute("historyMonths", historyMonths);
        model.addAttribute("historyDate", historyDate == null ? "" : historyDate);
        model.addAttribute("sessionActivities", roomSessionActivityService.getHistory(room, historyMonths, filterDate));
        return viewName;
    }

    @PostMapping("/rooms/{roomCode}/sessions/start")
    @ResponseBody
    public String startSession(@PathVariable String roomCode, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "Login required.";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "Admins can monitor rooms but cannot run focus sessions.";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null || !roomService.isUserMember(room, sessionUser)) {
            return "You are not a member of this room.";
        }
        if (!roomService.isRoomCreator(room, sessionUser)) {
            return "Only the room creator can control timer and breaks.";
        }

        return roomSessionActivityService.startSession(room, sessionUser);
    }

    @PostMapping("/rooms/{roomCode}/sessions/stop")
    @ResponseBody
    public String stopSession(@PathVariable String roomCode, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "Login required.";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "Admins can monitor rooms but cannot run focus sessions.";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null || !roomService.isUserMember(room, sessionUser)) {
            return "You are not a member of this room.";
        }
        if (!roomService.isRoomCreator(room, sessionUser)) {
            return "Only the room creator can control timer and breaks.";
        }

        return roomSessionActivityService.stopSession(room, sessionUser);
    }

    @PostMapping("/rooms/{roomCode}/sessions/break/start")
    @ResponseBody
    public String startBreak(@PathVariable String roomCode, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "Login required.";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "Admins can monitor rooms but cannot run focus sessions.";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null || !roomService.isUserMember(room, sessionUser)) {
            return "You are not a member of this room.";
        }
        if (!roomService.isRoomCreator(room, sessionUser)) {
            return "Only the room creator can control timer and breaks.";
        }

        return roomSessionActivityService.startBreak(room, sessionUser);
    }

    @PostMapping("/rooms/{roomCode}/sessions/break/end")
    @ResponseBody
    public String endBreak(@PathVariable String roomCode, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "Login required.";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            return "Admins can monitor rooms but cannot run focus sessions.";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null || !roomService.isUserMember(room, sessionUser)) {
            return "You are not a member of this room.";
        }
        if (!roomService.isRoomCreator(room, sessionUser)) {
            return "Only the room creator can control timer and breaks.";
        }

        return roomSessionActivityService.endBreak(room, sessionUser);
    }

    @PostMapping("/rooms/{roomCode}/schedule")
    public String createSchedule(
            @PathVariable String roomCode,
            @RequestParam String scheduleDate,
            @RequestParam String scheduleTime,
            @RequestParam(required = false) Integer durationPreset,
            @RequestParam(required = false) Integer durationCustom,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot create room schedules.");
            return "redirect:/admin/dashboard";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null) {
            return "redirect:/rooms/join";
        }
        if (!roomService.isRoomCreator(room, sessionUser)) {
            return "redirect:/rooms/" + roomCode.toUpperCase();
        }

        Integer duration = durationCustom != null && durationCustom > 0 ? durationCustom : durationPreset;
        try {
            roomScheduleService.createSchedule(
                    room,
                    sessionUser,
                    LocalDate.parse(scheduleDate),
                    LocalTime.parse(scheduleTime),
                    duration);
            session.setAttribute("flashRoomMessage", "Schedule created successfully.");
        } catch (Exception ex) {
            session.setAttribute("flashRoomError", ex.getMessage());
        }

        return "redirect:/rooms/" + roomCode.toUpperCase() + "/activity";
    }

    @PostMapping("/rooms/{roomCode}/schedule/{scheduleId}/delete")
    public String deleteSchedule(
            @PathVariable String roomCode,
            @PathVariable Long scheduleId,
            HttpSession session,
            Model model
    ) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        if (sessionUser.getRole() == UserRole.ADMIN) {
            session.setAttribute("flashAdminMessage", "Admin users cannot modify room schedules.");
            return "redirect:/admin/dashboard";
        }

        Room room = roomService.findByRoomCode(roomCode.toUpperCase()).orElse(null);
        if (room == null) {
            return "redirect:/rooms/join";
        }
        if (!roomService.isRoomCreator(room, sessionUser)) {
            return "redirect:/rooms/" + roomCode.toUpperCase();
        }

        try {
            roomScheduleService.deleteSchedule(room, sessionUser, scheduleId);
            session.setAttribute("flashRoomMessage", "Schedule deleted successfully.");
        } catch (Exception ex) {
            session.setAttribute("flashRoomError", ex.getMessage());
        }

        return "redirect:/rooms/" + roomCode.toUpperCase() + "/activity";
    }
}
