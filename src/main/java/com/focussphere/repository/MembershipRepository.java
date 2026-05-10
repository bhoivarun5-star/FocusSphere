package com.focussphere.repository;

import com.focussphere.model.Membership;
import com.focussphere.model.Room;
import com.focussphere.model.User;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    boolean existsByUserAndRoom(User user, Room room);

    long countByRoom(Room room);

    List<Membership> findByRoom(Room room);

    List<Membership> findByUser(User user);

    @Modifying
    @Query("delete from Membership m where m.user.id in :userIds")
    int deleteByUserIdIn(@Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query("delete from Membership m where m.room.createdBy.id in :userIds")
    int deleteByRoomCreatedByIdIn(@Param("userIds") Collection<Long> userIds);
}
