package com.focussphere.service;

import com.focussphere.model.JoinRequestStatus;
import com.focussphere.model.RoomVisibility;
import com.focussphere.model.UserRole;
import com.focussphere.repository.JoinRequestRepository;
import com.focussphere.repository.MembershipRepository;
import com.focussphere.repository.MessageRepository;
import com.focussphere.repository.RoomRepository;
import com.focussphere.repository.RoomScheduleRepository;
import com.focussphere.repository.RoomSessionActivityRepository;
import com.focussphere.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AdminInsightsService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final MembershipRepository membershipRepository;
    private final MessageRepository messageRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final RoomSessionActivityRepository roomSessionActivityRepository;
    private final RoomScheduleRepository roomScheduleRepository;

    public AdminInsightsService(
            UserRepository userRepository,
            RoomRepository roomRepository,
            MembershipRepository membershipRepository,
            MessageRepository messageRepository,
            JoinRequestRepository joinRequestRepository,
            RoomSessionActivityRepository roomSessionActivityRepository,
            RoomScheduleRepository roomScheduleRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.membershipRepository = membershipRepository;
        this.messageRepository = messageRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.roomSessionActivityRepository = roomSessionActivityRepository;
        this.roomScheduleRepository = roomScheduleRepository;
    }

    public Map<String, Long> getAnalytics() {
        Map<String, Long> data = new LinkedHashMap<>();

        long totalUsers = userRepository.count();
        long adminUsers = userRepository.countByRole(UserRole.ADMIN);
        long studentUsers = userRepository.countByRole(UserRole.USER);

        long totalRooms = roomRepository.count();
        long publicRooms = roomRepository.countByVisibility(RoomVisibility.PUBLIC);
        long privateRooms = roomRepository.countByVisibility(RoomVisibility.PRIVATE);

        long totalMessages = messageRepository.count();
        long totalMemberships = membershipRepository.count();

        long activeSessions = roomSessionActivityRepository.countBySessionEndIsNull();
        long sessionsToday = roomSessionActivityRepository.countBySessionStartAfter(LocalDate.now().atStartOfDay());

        data.put("totalUsers", totalUsers);
        data.put("adminUsers", adminUsers);
        data.put("studentUsers", studentUsers);
        data.put("totalRooms", totalRooms);
        data.put("publicRooms", publicRooms);
        data.put("privateRooms", privateRooms);
        data.put("totalMemberships", totalMemberships);
        data.put("totalMessages", totalMessages);
        data.put("activeSessions", activeSessions);
        data.put("sessionsToday", sessionsToday);

        return data;
    }

    public Map<String, Object> getSystemReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        long pendingJoinRequests = joinRequestRepository.countByStatus(JoinRequestStatus.PENDING);
        long approvedJoinRequests = joinRequestRepository.countByStatus(JoinRequestStatus.APPROVED);
        long rejectedJoinRequests = joinRequestRepository.countByStatus(JoinRequestStatus.REJECTED);
        long totalJoinDecisions = approvedJoinRequests + rejectedJoinRequests;

        long totalRooms = roomRepository.count();
        long totalMemberships = membershipRepository.count();
        long totalMessages = messageRepository.count();
        long activeSessions = roomSessionActivityRepository.countBySessionEndIsNull();

        long schedulesToday = roomScheduleRepository.countByScheduleDate(LocalDate.now());
        long schedulesNextWeek = roomScheduleRepository.countByScheduleDateBetween(LocalDate.now(), LocalDate.now().plusDays(7));
        long upcomingSchedules = roomScheduleRepository.countByScheduleDateGreaterThanEqual(LocalDate.now());

        double approvalRate = totalJoinDecisions == 0 ? 0.0 : (approvedJoinRequests * 100.0) / totalJoinDecisions;
        double avgMembersPerRoom = totalRooms == 0 ? 0.0 : (totalMemberships * 1.0) / totalRooms;
        double avgMessagesPerRoom = totalRooms == 0 ? 0.0 : (totalMessages * 1.0) / totalRooms;
        double avgSessionDurationMinutes = (roomSessionActivityRepository.findAverageCompletedDurationSeconds() == null ? 0.0
                : roomSessionActivityRepository.findAverageCompletedDurationSeconds()) / 60.0;

        report.put("generatedAt", now);
        report.put("pendingJoinRequests", pendingJoinRequests);
        report.put("approvedJoinRequests", approvedJoinRequests);
        report.put("rejectedJoinRequests", rejectedJoinRequests);
        report.put("joinApprovalRate", String.format("%.1f%%", approvalRate));
        report.put("upcomingSchedules", upcomingSchedules);
        report.put("schedulesToday", schedulesToday);
        report.put("schedulesNextWeek", schedulesNextWeek);
        report.put("activeSessions", activeSessions);
        report.put("avgMembersPerRoom", String.format("%.2f", avgMembersPerRoom));
        report.put("avgMessagesPerRoom", String.format("%.2f", avgMessagesPerRoom));
        report.put("avgSessionDurationMinutes", String.format("%.1f", avgSessionDurationMinutes));

        return report;
    }
}
