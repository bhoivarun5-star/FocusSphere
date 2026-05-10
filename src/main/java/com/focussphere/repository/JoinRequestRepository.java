package com.focussphere.repository;

import com.focussphere.model.JoinRequest;
import com.focussphere.model.JoinRequestStatus;
import com.focussphere.model.Room;
import com.focussphere.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    Optional<JoinRequest> findByRoomAndRequester(Room room, User requester);

    List<JoinRequest> findByRequesterOrderByRequestedAtDesc(User requester);

    List<JoinRequest> findByRoomCreatedByAndStatusOrderByRequestedAtAsc(User creator, JoinRequestStatus status);

    Optional<JoinRequest> findByIdAndRoomCreatedBy(Long id, User creator);

    long countByRequesterAndStatus(User requester, JoinRequestStatus status);

    long countByStatus(JoinRequestStatus status);
}