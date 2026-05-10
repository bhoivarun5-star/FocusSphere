package com.focussphere.repository;

import com.focussphere.model.Message;
import com.focussphere.model.Room;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {
    long countByRoom(Room room);

    List<Message> findByRoomOrderBySentAtAsc(Room room);

    @Modifying
    @Query("delete from Message m where m.sender.id in :userIds")
    int deleteBySenderIdIn(@Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query("delete from Message m where m.room.createdBy.id in :userIds")
    int deleteByRoomCreatedByIdIn(@Param("userIds") Collection<Long> userIds);
}
