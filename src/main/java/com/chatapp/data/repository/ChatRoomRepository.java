package com.chatapp.data.repository;

import com.chatapp.data.entity.ChatRoom;
import com.chatapp.data.entity.User;
import com.chatapp.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUser1AndUser2(User sender, User receiver);
    Optional<ChatRoom> findByUser1AndUser2AndRoomType(
            User user1,
            User user2,
            RoomType roomType
    );
}
