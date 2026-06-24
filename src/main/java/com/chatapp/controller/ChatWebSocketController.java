package com.chatapp.controller;

import com.chatapp.data.entity.Message;
import com.chatapp.dto.request.ChatMessageRequest;
import com.chatapp.dto.request.TypingMessage;
import com.chatapp.dto.response.ChatMessageResponse;
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

            Long roomId = savedMessage.getChatRoom().getId();

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + roomId,
                    map(savedMessage)
            );
        }

    }

    private ChatMessageResponse map(Message message) {

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getUsername())
                .receiverId(
                        message.getReceiver() != null
                                ? message.getReceiver().getId()
                                : null
                )
                .groupId(
                        message.getGroup() != null
                                ? message.getGroup().getId()
                                : null
                )
                .message(message.getMessage())
                .attachmentUrl(message.getAttachment())
                .messageType(message.getMessageType().name())
                .status(message.getMessageStatus().name())
                .createdAt(message.getCreatedAt())
                .build();
    }

    @MessageMapping("/chat.typing")
    public void typing(TypingMessage message) {
        messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId() + "/typing", message);
    }

}
