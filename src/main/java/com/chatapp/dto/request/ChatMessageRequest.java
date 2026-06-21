package com.chatapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {

    private Long senderId;
    private Long receiverId;
    private Long groupId;

    private String message;

    private Long attachmentId;
}