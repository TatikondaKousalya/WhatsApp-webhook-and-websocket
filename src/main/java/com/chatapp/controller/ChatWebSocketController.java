package com.chatapp.controller;

import com.chatapp.data.entity.Message;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.dto.request.ChatMessageRequest;
import com.chatapp.dto.request.PresenceMessage;
import com.chatapp.dto.request.TypingMessage;
import com.chatapp.dto.response.ChatMessageResponse;
import com.chatapp.exception.ResourceNotFoundException;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void send(ChatMessageRequest request) {

        Message savedMessage;

        if (request.getGroupId() != null) {

            savedMessage = messageService.sendGroupMessage(
                    request.getSenderId(),
                    request.getGroupId(),
                    request.getMessage(),
                    request.getAttachmentId()
            );

            messagingTemplate.convertAndSend(
                    "/topic/group/" + request.getGroupId(),
                    map(savedMessage)
            );

        } else {

            savedMessage = messageService.sendPrivateMessage(
                    request.getSenderId(),
                    request.getReceiverId(),
                    request.getMessage(),
                    request.getAttachmentId()
            );

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + savedMessage.getChatRoomId(),
                    map(savedMessage)
            );
        }
    }

    private ChatMessageResponse map(Message message) {

        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Sender not found."));

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .senderId(message.getSenderId())
                .senderName(sender.getUsername())
                .receiverId(message.getReceiverId())
                .groupId(message.getGroupId())
                .message(message.getMessage())
                .attachmentUrl(message.getAttachment())
                .messageType(message.getMessageType().name())
                .status(message.getMessageStatus().name())
                .createdAt(message.getCreatedAt())
                .build();
    }

    @MessageMapping("/chat.typing")
    public void typing(TypingMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getRoomId() + "/typing",
                message
        );
    }

    @MessageMapping("/chat.online")
    public void online(PresenceMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/presence",
                message
        );
    }

    @MessageMapping("/chat.offline")
    public void offline(PresenceMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/presence",
                message
        );
    }
}