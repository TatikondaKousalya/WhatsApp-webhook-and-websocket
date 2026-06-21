package com.chatapp.data.repository;

import com.chatapp.data.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
}
