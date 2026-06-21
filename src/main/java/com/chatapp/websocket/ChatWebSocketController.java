package com.chatapp.websocket;

import com.chatapp.data.entity.Message;
import com.chatapp.dto.request.ChatMessageRequest;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;

    /**
     * Private Chat
     * Client sends to: /app/chat/private
     * Subscribers: /topic/private
     */
    @MessageMapping("/chat/private")
    @SendTo("/topic/private")
    public Message sendPrivateMessage(
            @Payload ChatMessageRequest request) {

        return messageService.sendPrivateMessage(
                request.getSenderId(),
                request.getReceiverId(),
                request.getMessage(),
                request.getAttachmentId()
        );
    }

    /**
     * Group Chat
     * Client sends to: /app/chat/group
     * Subscribers: /topic/group
     */
    @MessageMapping("/chat/group")
    @SendTo("/topic/group")
    public Message sendGroupMessage(
            @Payload ChatMessageRequest request) {

        return messageService.sendGroupMessage(
                request.getSenderId(),
                request.getGroupId(),
                request.getMessage(),
                request.getAttachmentId()
        );
    }
}