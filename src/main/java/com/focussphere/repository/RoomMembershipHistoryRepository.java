package com.focussphere.repository;

import com.focussphere.model.Room;
import com.focussphere.model.RoomMembershipHistory;
import com.focussphere.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomMembershipHistoryRepository extends JpaRepository<RoomMembershipHistory, Long> {
    List<RoomMembershipHistory> findByUserOrderByJoinedAtDesc(User user);

    List<RoomMembershipHistory> findByUserAndLeftAtIsNullOrderByJoinedAtDesc(User user);

    Optional<RoomMembershipHistory> findFirstByUserAndRoomAndLeftAtIsNullOrderByJoinedAtDesc(User user, Room room);
}
