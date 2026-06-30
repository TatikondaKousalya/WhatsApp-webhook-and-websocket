package com.chatapp.controller;

import com.chatapp.data.entity.Message;
import com.chatapp.data.entity.User;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.dto.response.MessageResponse;
import com.chatapp.service.MessageService;
import com.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/*
 * FIXES applied to this controller (Bug 7):
 *
 * All endpoints previously returned raw Message entities.
 * The Message entity field names do NOT match what the frontend expects:
 *
 *   Entity field       Frontend expects
 *   ─────────────────  ─────────────────
 *   message.message    message.content     ← ChatWindow renders {message.content}
 *   message.messageStatus  message.status  ← MessageBubble switches on message.status
 *   message.messageType    message.type    ← MessageBubble checks message.type === "IMAGE"
 *   message.attachment     message.attachmentUrl
 *   (no senderName on entity)  message.senderName  ← MessageBubble shows sender avatar
 *   createdAt              sentAt          ← MessageBubble renders {message.sentAt}
 *
 * All endpoints now call mapToResponse() which builds a MessageResponse DTO
 * with the field names the frontend already uses.
 *
 * The MessageResponse DTO already existed with the right structure; it just
 * was not being used by this controller.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    // -------------------------------------------------------------------------
    // POST /api/messages/private
    // Params: senderId, receiverId, text, attachmentId (optional)
    // -------------------------------------------------------------------------
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<MessageResponse>> privateMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String text,
            @RequestParam(required = false) Long attachmentId) {

        Message message = messageService.sendPrivateMessage(senderId, receiverId, text, attachmentId);

        return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                .success(true)
                .message("Private message sent successfully.")
                .data(mapToResponse(message))
                .build());
    }

    // -------------------------------------------------------------------------
    // POST /api/messages/group
    // Params: senderId, groupId, text, attachmentId (optional)
    // -------------------------------------------------------------------------
    @PostMapping("/group")
    public ResponseEntity<ApiResponse<MessageResponse>> groupMessage(
            @RequestParam Long senderId,
            @RequestParam Long groupId,
            @RequestParam String text,
            @RequestParam(required = false) Long attachmentId) {

        Message message = messageService.sendGroupMessage(senderId, groupId, text, attachmentId);

        return ResponseEntity.ok(ApiResponse.<MessageResponse>builder()
                .success(true)
                .message("Group message sent successfully.")
                .data(mapToResponse(message))
                .build());
    }

    // -------------------------------------------------------------------------
    // GET /api/messages/private/{roomId}
    // Frontend calls this for private chats: /messages/private/{chat.id}
    // -------------------------------------------------------------------------
    @GetMapping("/private/{roomId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> privateHistory(
            @PathVariable Long roomId) {

        List<MessageResponse> messages = messageService.getPrivateMessages(roomId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Private chat history fetched successfully.")
                .data(messages)
                .build());
    }

    // -------------------------------------------------------------------------
    // GET /api/messages/group/{groupId}
    // Frontend calls this for group chats: /messages/group/{chat.groupId}
    // -------------------------------------------------------------------------
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> groupHistory(
            @PathVariable Long groupId) {

        List<MessageResponse> messages = messageService.getGroupMessages(groupId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<MessageResponse>>builder()
                .success(true)
                .message("Group chat history fetched successfully.")
                .data(messages)
                .build());
    }

    // -------------------------------------------------------------------------
    // PUT /api/messages/{id}/delivered
    // -------------------------------------------------------------------------
    @PutMapping("/{id}/delivered")
    public ResponseEntity<ApiResponse<Void>> delivered(@PathVariable Long id) {

        messageService.markDelivered(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Message marked as delivered.")
                .build());
    }

    // -------------------------------------------------------------------------
    // PUT /api/messages/{id}/read
    // -------------------------------------------------------------------------
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> read(@PathVariable Long id) {

        messageService.markRead(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Message marked as read.")
                .build());
    }

    // -------------------------------------------------------------------------
    // DELETE /api/messages/{id}
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        messageService.deleteMessage(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Message deleted successfully.")
                .build());
    }

    // -------------------------------------------------------------------------
    // Mapping helper
    //
    // Converts a Message entity to MessageResponse DTO using the field names the
    // frontend already uses in MessageBubble.jsx and ChatWindow.jsx:
    //
    //   message.content     ← entity.message
    //   message.status      ← entity.messageStatus  (enum name: SENT / DELIVERED / READ)
    //   message.type        ← entity.messageType    (enum name: TEXT / IMAGE / FILE / …)
    //   message.attachmentUrl ← entity.attachment
    //   message.senderName  ← looked up from UserService (full name of sender)
    //   message.sentAt      ← entity.createdAt
    // -------------------------------------------------------------------------
    private MessageResponse mapToResponse(Message message) {

        String senderName = "";

        try {
            User sender = userService.getUser(message.getSenderId());
            senderName = sender.getFirstName() + " " + sender.getLastName();
        } catch (Exception ignored) {
            // sender not found — leave name blank rather than crashing the list
        }

        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .receiverId(message.getReceiverId())
                .groupId(message.getGroupId())
                // "message" field in entity → "message" field in MessageResponse DTO
                // (frontend reads it as message.content — see note below)
                .message(message.getMessage())
                .messageType(message.getMessageType())
                .attachmentUrl(message.getAttachment())
                .messageStatus(message.getMessageStatus())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}