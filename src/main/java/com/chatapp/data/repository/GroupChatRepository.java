package com.chatapp.data.repository;

import com.chatapp.data.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 * ADDITION: findByRoomId is needed by the updated ChatService to look up
 * the GroupChat record when building ChatRoomResponse for GROUP-type rooms.
 */
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {

    Optional<GroupChat> findByRoomId(Long roomId);
}