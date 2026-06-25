package com.chatapp.data.repository;

import com.chatapp.data.entity.ChatRoom;
import com.chatapp.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUser1IdAndUser2IdAndRoomType(
            Long user1Id,
            Long user2Id,
            RoomType roomType
    );
}