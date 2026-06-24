package com.chatapp.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String content;
    private String type;
    private String attachmentUrl;
    private String status;
    private LocalDateTime sentAt;
    private Long messageId;
    private Long receiverId;
    private Long groupId;
    private String message;
    private String messageType;
    private LocalDateTime createdAt;

}