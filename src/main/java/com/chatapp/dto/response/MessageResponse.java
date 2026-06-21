package com.chatapp.dto.response;

import com.chatapp.enums.MessageStatus;
import com.chatapp.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long id;

    private Long senderId;

    private String senderName;

    // For one-to-one chat
    private Long receiverId;

    // For group chat
    private Long groupId;

    private String message;

    private MessageType messageType;

    // Attachment URL (image, video, document, etc.)
    private String attachmentUrl;

    // SENT, DELIVERED, READ
    private MessageStatus messageStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}