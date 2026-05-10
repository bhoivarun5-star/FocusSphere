package com.focussphere.service;

import com.focussphere.dto.RoomMonitorRow;
import com.focussphere.dto.RoomDiscoveryRow;
import com.focussphere.model.JoinRequest;
import com.focussphere.model.JoinRequestStatus;
import com.focussphere.model.Membership;
import com.focussphere.model.NotificationType;
import com.focussphere.model.Room;
import com.focussphere.model.RoomMembershipHistory;
import com.focussphere.model.RoomVisibility;
import com.focussphere.model.User;
import com.focussphere.model.UserRole;
import com.focussphere.repository.JoinRequestRepository;
import com.focussphere.repository.MembershipRepository;
import com.focussphere.repository.MessageRepository;
import com.focussphere.repository.RoomMembershipHistoryRepository;
import com.focussphere.repository.RoomRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MembershipRepository membershipRepository;
    private final MessageRepository messageRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final RoomMembershipHistoryRepository roomMembershipHistoryRepository;
    private final NotificationService notificationService;

    public RoomService(
            RoomRepository roomRepository,
            MembershipRepository membershipRepository,
            MessageRepository messageRepository,
            JoinRequestRepository joinRequestRepository,
            RoomMembershipHistoryRepository roomMembershipHistoryRepository,
            NotificationService notificationService
    ) {
        this.roomRepository = roomRepository;
        this.membershipRepository = membershipRepository;
        this.messageRepository = messageRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.roomMembershipHistoryRepository = roomMembershipHistoryRepository;
        this.notificationService = notificationService;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getPublicRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(room -> room.getVisibility() != RoomVisibility.PRIVATE)
                .collect(Collectors.toList());
    }

    public List<Room> getRoomsCreatedBy(User creator) {
        return roomRepository.findByCreatedBy(creator);
    }

    public List<Room> getRoomsCreatedByOthers(User user) {
        return roomRepository.findByCreatedByNot(user);
    }

    public List<Room> getPublicRoomsCreatedByOthers(User user) {
        return roomRepository.findByCreatedByNot(user)
                .stream()
                .filter(room -> room.getVisibility() == null || room.getVisibility() == RoomVisibility.PUBLIC)
                .collect(Collectors.toList());
    }

    public List<Room> getAvailableRoomsForUser(User user) {
        if (user == null) {
            return List.of();
        }
        if (user.getRole() == UserRole.ADMIN) {
            return roomRepository.findAll();
        }

        return roomRepository.findAll()
                .stream()
                .filter(room -> room.getVisibility() != RoomVisibility.PRIVATE || isUserApprovedForRoom(room, user))
                .collect(Collectors.toList());
    }

    public List<RoomDiscoveryRow> getRoomDiscoveryRows(User user, String sortBy, String visibilityFilter, String searchTerm) {
        List<RoomDiscoveryRow> rows = getAvailableRoomsForUser(user)
                .stream()
                .map(room -> new RoomDiscoveryRow(
                        room.getRoomName(),
                        room.getRoomCode(),
                        room.getRoomDescription(),
                        room.getVisibility() == null ? RoomVisibility.PUBLIC.name() : room.getVisibility().name(),
                        room.getCreatedBy() == null ? "Unknown" : room.getCreatedBy().getName(),
                        membershipRepository.countByRoom(room),
                        messageRepository.countByRoom(room),
                    user != null && (user.getRole() == UserRole.ADMIN || isUserApprovedForRoom(room, user))))
                .collect(Collectors.toCollection(ArrayList::new));

            String normalizedSearch = searchTerm == null ? "" : searchTerm.trim().toLowerCase(Locale.ROOT);
            if (!normalizedSearch.isBlank()) {
                rows = rows.stream()
                    .filter(row -> containsIgnoreCase(row.getRoomName(), normalizedSearch)
                        || containsIgnoreCase(row.getDescription(), normalizedSearch)
                        || containsIgnoreCase(row.getCreatedBy(), normalizedSearch)
                        || containsIgnoreCase(row.getVisibility(), normalizedSearch))
                    .collect(Collectors.toCollection(ArrayList::new));
            }

        String normalizedFilter = visibilityFilter == null ? "all" : visibilityFilter.trim().toLowerCase(Locale.ROOT);
        if (!"all".equals(normalizedFilter)) {
            rows = rows.stream()
                    .filter(row -> row.getVisibility().equalsIgnoreCase(normalizedFilter))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        Comparator<RoomDiscoveryRow> comparator;
        switch ((sortBy == null ? "" : sortBy.trim().toLowerCase(Locale.ROOT))) {
            case "name-desc":
                comparator = Comparator.comparing(RoomDiscoveryRow::getRoomName, String.CASE_INSENSITIVE_ORDER).reversed();
                break;
            case "members-desc":
                comparator = Comparator.comparingLong(RoomDiscoveryRow::getMemberCount).reversed();
                break;
            case "members-asc":
                comparator = Comparator.comparingLong(RoomDiscoveryRow::getMemberCount);
                break;
            case "messages-desc":
                comparator = Comparator.comparingLong(RoomDiscoveryRow::getMessageCount).reversed();
                break;
            case "visibility":
                comparator = Comparator.comparing(RoomDiscoveryRow::getVisibility, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(RoomDiscoveryRow::getRoomName, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comparator = Comparator.comparing(RoomDiscoveryRow::getRoomName, String.CASE_INSENSITIVE_ORDER);
                break;
        }
        rows.sort(comparator);
        return rows;
    }

    private boolean containsIgnoreCase(String value, String normalizedSearch) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedSearch);
    }

    public List<com.focussphere.model.JoinRequest> getJoinRequestsForRequester(User requester) {
        return joinRequestRepository.findByRequesterOrderByRequestedAtDesc(requester);
    }

    public Room createRoom(String roomName, String roomDescription, RoomVisibility visibility, User creator) {
        Room room = new Room();
        room.setRoomName(roomName);
        room.setRoomCode(generateRoomCode());
        room.setRoomDescription(roomDescription);
        RoomVisibility safeVisibility = visibility == null ? RoomVisibility.PUBLIC : visibility;
        room.setVisibility(safeVisibility);
        room.setPrivateAccessCode(safeVisibility == RoomVisibility.PRIVATE ? generatePrivateAccessCode() : null);
        room.setCreatedBy(creator);
        Room savedRoom = roomRepository.save(room);

        Membership membership = new Membership();
        membership.setUser(creator);
        membership.setRoom(savedRoom);
        membershipRepository.save(membership);
        recordJoinHistory(creator, savedRoom);

        return savedRoom;
    }

    public Room joinRoom(String code, User user) {
        Room room = findRoomByAnyCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Room not found for the provided code."));

        if (!isUserApprovedForRoom(room, user)) {
            throw new IllegalArgumentException("You must send a join request and wait for creator approval.");
        }

        ensureMembership(room, user);
        return room;
    }

    @Transactional
    public String requestToJoinRoom(String roomCodeOrPrivateCode, User requester) {
        Room room = findRoomByAnyCode(roomCodeOrPrivateCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found for the provided code."));

        if (isRoomCreator(room, requester) || isUserMember(room, requester)) {
            return "You already have access to this room.";
        }

        JoinRequest joinRequest = joinRequestRepository.findByRoomAndRequester(room, requester).orElse(null);
        if (joinRequest == null) {
            joinRequest = new JoinRequest();
            joinRequest.setRoom(room);
            joinRequest.setRequester(requester);
            joinRequest.setRequestedAt(java.time.LocalDateTime.now());
        }

        if (joinRequest.getStatus() == JoinRequestStatus.PENDING) {
            return "Your request is already pending creator approval.";
        }

        joinRequest.setStatus(JoinRequestStatus.PENDING);
        joinRequest.setReviewedAt(null);
        joinRequestRepository.save(joinRequest);

        // Create notification for the room creator
        try {
            notificationService.createNotification(
                room.getCreatedBy(),
                NotificationType.JOIN_REQUEST,
                "Join request from " + requester.getName(),
                requester.getName() + " requested to join room: " + room.getRoomName(),
                room,
                requester
            );
        } catch (Exception e) {
            // Log error but don't fail the join request
            System.err.println("Error creating join request notification: " + e.getMessage());
        }

        return "Join request sent. You can enter after creator approval.";
    }

    @Transactional
    public void approveJoinRequest(Long requestId, User creator) {
        JoinRequest joinRequest = joinRequestRepository.findByIdAndRoomCreatedBy(requestId, creator)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        joinRequest.setStatus(JoinRequestStatus.APPROVED);
        joinRequest.setReviewedAt(java.time.LocalDateTime.now());
        joinRequestRepository.save(joinRequest);

        ensureMembership(joinRequest.getRoom(), joinRequest.getRequester());
    }

    @Transactional
    public void rejectJoinRequest(Long requestId, User creator) {
        JoinRequest joinRequest = joinRequestRepository.findByIdAndRoomCreatedBy(requestId, creator)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found."));

        joinRequest.setStatus(JoinRequestStatus.REJECTED);
        joinRequest.setReviewedAt(java.time.LocalDateTime.now());
        joinRequestRepository.save(joinRequest);
    }

    public List<JoinRequest> getPendingJoinRequestsForCreator(User creator) {
        return joinRequestRepository.findByRoomCreatedByAndStatusOrderByRequestedAtAsc(creator, JoinRequestStatus.PENDING);
    }

    public long getPendingJoinRequestCountForUser(User requester) {
        return joinRequestRepository.countByRequesterAndStatus(requester, JoinRequestStatus.PENDING);
    }

    public boolean isRoomCreator(Room room, User user) {
        return room != null && user != null && room.getCreatedBy() != null && room.getCreatedBy().getId().equals(user.getId());
    }

    public boolean isUserApprovedForRoom(Room room, User user) {
        if (user == null || room == null) {
            return false;
        }
        if (user.getRole() == UserRole.ADMIN || isRoomCreator(room, user) || isUserMember(room, user)) {
            return true;
        }
        return joinRequestRepository.findByRoomAndRequester(room, user)
                .map(request -> request.getStatus() == JoinRequestStatus.APPROVED)
                .orElse(false);
    }

    public Optional<Room> findRoomByAnyCode(String code) {
        Optional<Room> byRoomCode = roomRepository.findByRoomCode(code);
        if (byRoomCode.isPresent()) {
            return byRoomCode;
        }
        return roomRepository.findByPrivateAccessCode(code);
    }

    public Optional<Room> findByRoomCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode);
    }

    public List<User> getRoomMembers(Room room) {
        List<Membership> memberships = membershipRepository.findByRoom(room);
        List<User> users = new ArrayList<>();
        for (Membership membership : memberships) {
            users.add(membership.getUser());
        }
        return users;
    }

    public boolean isUserMember(Room room, User user) {
        return membershipRepository.existsByUserAndRoom(user, room);
    }

    public List<Room> getCurrentJoinedRooms(User user) {
        if (user == null) {
            return List.of();
        }
        return membershipRepository.findByUser(user)
                .stream()
                .map(Membership::getRoom)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Transactional
    public List<RoomMembershipHistory> getMembershipHistoryForProfile(User user) {
        if (user == null) {
            return List.of();
        }
        syncMembershipHistoryForUser(user);
        return roomMembershipHistoryRepository.findByUserOrderByJoinedAtDesc(user);
    }

    private void ensureMembership(Room room, User user) {
        if (!membershipRepository.existsByUserAndRoom(user, room)) {
            Membership membership = new Membership();
            membership.setUser(user);
            membership.setRoom(room);
            membershipRepository.save(membership);
            recordJoinHistory(user, room);
        }
    }

    private void recordJoinHistory(User user, Room room) {
        if (user == null || room == null) {
            return;
        }
        boolean hasOpenEntry = roomMembershipHistoryRepository
                .findFirstByUserAndRoomAndLeftAtIsNullOrderByJoinedAtDesc(user, room)
                .isPresent();
        if (hasOpenEntry) {
            return;
        }

        RoomMembershipHistory history = new RoomMembershipHistory();
        history.setUser(user);
        history.setRoom(room);
        history.setJoinedAt(LocalDateTime.now());
        roomMembershipHistoryRepository.save(history);
    }

    private void syncMembershipHistoryForUser(User user) {
        Set<Long> activeRoomIds = membershipRepository.findByUser(user)
                .stream()
                .map(Membership::getRoom)
                .filter(java.util.Objects::nonNull)
                .map(Room::getId)
                .collect(Collectors.toSet());

        List<RoomMembershipHistory> openEntries = roomMembershipHistoryRepository.findByUserAndLeftAtIsNullOrderByJoinedAtDesc(user);
        for (RoomMembershipHistory entry : openEntries) {
            Room room = entry.getRoom();
            if (room == null || room.getId() == null) {
                continue;
            }
            if (!activeRoomIds.contains(room.getId())) {
                entry.setLeftAt(LocalDateTime.now());
                roomMembershipHistoryRepository.save(entry);
            }
        }
    }

    public List<RoomMonitorRow> getRoomMonitorRows() {
        List<RoomMonitorRow> rows = new ArrayList<>();
        for (Room room : roomRepository.findAll()) {
            long memberCount = membershipRepository.countByRoom(room);
            long messageCount = messageRepository.countByRoom(room);
            rows.add(new RoomMonitorRow(
                    room.getRoomName(),
                    room.getRoomCode(),
                    room.getVisibility() == null ? RoomVisibility.PUBLIC.name() : room.getVisibility().name(),
                    room.getCreatedBy().getName(),
                    memberCount,
                    messageCount
            ));
        }
        return rows;
    }

    public List<RoomMonitorRow> getRoomMonitorRowsForCreator(User creator) {
        List<RoomMonitorRow> rows = new ArrayList<>();
        for (Room room : roomRepository.findByCreatedBy(creator)) {
            long memberCount = membershipRepository.countByRoom(room);
            long messageCount = messageRepository.countByRoom(room);
            rows.add(new RoomMonitorRow(
                    room.getRoomName(),
                    room.getRoomCode(),
                    room.getVisibility() == null ? RoomVisibility.PUBLIC.name() : room.getVisibility().name(),
                    room.getCreatedBy().getName(),
                    memberCount,
                    messageCount
            ));
        }
        return rows;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generatePrivateAccessCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (roomRepository.existsByPrivateAccessCode(code));
        return code;
    }
}
