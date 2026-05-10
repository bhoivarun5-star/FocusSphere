package com.focussphere.service;

import com.focussphere.model.Membership;
import com.focussphere.model.RoomSchedule;
import com.focussphere.model.User;
import com.focussphere.model.UserRole;
import com.focussphere.repository.MembershipRepository;
import com.focussphere.repository.RoomScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomScheduleAutomationService {

    private final RoomScheduleRepository roomScheduleRepository;
    private final MembershipRepository membershipRepository;
    private final RoomSessionActivityService roomSessionActivityService;

    public RoomScheduleAutomationService(
            RoomScheduleRepository roomScheduleRepository,
            MembershipRepository membershipRepository,
            RoomSessionActivityService roomSessionActivityService) {
        this.roomScheduleRepository = roomScheduleRepository;
        this.membershipRepository = membershipRepository;
        this.roomSessionActivityService = roomSessionActivityService;
    }

    @Transactional
    @Scheduled(fixedDelay = 30000)
    public void processSchedules() {
        LocalDateTime now = LocalDateTime.now();
        List<RoomSchedule> schedules = roomScheduleRepository.findAll();

        for (RoomSchedule schedule : schedules) {
            LocalDateTime scheduleStart = LocalDateTime.of(schedule.getScheduleDate(), schedule.getScheduleTime());
            LocalDateTime scheduleEnd = scheduleStart.plusMinutes(schedule.getDurationMinutes() == null ? 0 : schedule.getDurationMinutes());

            if (!now.isBefore(scheduleStart) && now.isBefore(scheduleEnd)) {
                startScheduledSession(schedule, scheduleStart);
            }

            if (!now.isBefore(scheduleEnd)) {
                stopScheduledSession(schedule, scheduleEnd);
            }
        }
    }

    private void startScheduledSession(RoomSchedule schedule, LocalDateTime startAt) {
        for (Membership membership : membershipRepository.findByRoom(schedule.getRoom())) {
            User user = membership.getUser();
            if (user != null && user.getRole() == UserRole.USER) {
                roomSessionActivityService.startSessionAt(schedule.getRoom(), user, startAt);
            }
        }
    }

    private void stopScheduledSession(RoomSchedule schedule, LocalDateTime endAt) {
        for (Membership membership : membershipRepository.findByRoom(schedule.getRoom())) {
            User user = membership.getUser();
            if (user != null && user.getRole() == UserRole.USER) {
                roomSessionActivityService.stopSessionAt(schedule.getRoom(), user, endAt);
            }
        }
    }
}
