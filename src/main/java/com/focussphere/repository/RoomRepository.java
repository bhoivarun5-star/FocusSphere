package com.focussphere.repository;

import com.focussphere.model.Room;
import com.focussphere.model.RoomVisibility;
import com.focussphere.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomCode(String roomCode);
    Optional<Room> findByPrivateAccessCode(String privateAccessCode);
    List<Room> findByVisibility(RoomVisibility visibility);
    List<Room> findByCreatedBy(User createdBy);
    List<Room> findByCreatedByNot(User createdBy);
    long countByVisibility(RoomVisibility visibility);
    boolean existsByPrivateAccessCode(String privateAccessCode);

    @Modifying
    @Query("delete from Room r where r.createdBy.id in :userIds")
    int deleteByCreatedByIdIn(@Param("userIds") Collection<Long> userIds);
}
