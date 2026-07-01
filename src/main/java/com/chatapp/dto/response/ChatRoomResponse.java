package com.chatapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {

    private Long id;

    private String name;

    private String roomType;

    private Long receiverId;

    private Long groupId;

    private String profilePicture;

    private Boolean online;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long lastSenderId;
    private String lastSenderName;
}