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
public class ChatMessageResponse {

    private Long messageId;

    private Long chatRoomId;

    private Long senderId;

    private String senderName;

    private Long receiverId;

    private Long groupId;

    private String message;

    private String messageType;

    private String attachmentUrl;

    private String status;

    private LocalDateTime createdAt;
}