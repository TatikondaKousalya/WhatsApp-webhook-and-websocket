package com.chatapp.controller;

import com.chatapp.data.entity.Message;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // Send private message
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<Message>> privateMessage(@RequestParam Long senderId, @RequestParam Long receiverId,
                                                               @RequestParam String text, @RequestParam(required = false) Long attachmentId) {
        Message message = messageService.sendPrivateMessage(senderId, receiverId, text, attachmentId);
        return ResponseEntity.ok(ApiResponse.<Message>builder()
                .success(true).message("Private message sent successfully.")
                .data(message).build());
    }

    // Send group message
    @PostMapping("/group")
    public ResponseEntity<ApiResponse<Message>> groupMessage(@RequestParam Long senderId, @RequestParam Long groupId,
                                                             @RequestParam String text, @RequestParam(required = false) Long attachmentId) {
        Message message = messageService.sendGroupMessage(senderId, groupId, text, attachmentId);
        return ResponseEntity.ok(ApiResponse.<Message>builder()
                .success(true).message("Group message sent successfully.")
                .data(message).build());
    }

    // Private chat history
    @GetMapping("/private/{roomId}")
    public ResponseEntity<ApiResponse<List<Message>>> privateHistory(@PathVariable Long roomId) {
        List<Message> messages = messageService.getPrivateMessages(roomId);
        return ResponseEntity.ok(ApiResponse.<List<Message>>builder()
                .success(true).message("Private chat history fetched successfully.")
                .data(messages).build());
    }

    // Group chat history
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<Message>>> groupHistory(@PathVariable Long groupId) {
        List<Message> messages = messageService.getGroupMessages(groupId);
        return ResponseEntity.ok(ApiResponse.<List<Message>>builder()
                .success(true).message("Group chat history fetched successfully.")
                .data(messages).build());
    }

    @PutMapping("/{id}/delivered")
    public ResponseEntity<ApiResponse<Void>> delivered(@PathVariable Long id) {
        messageService.markDelivered(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Message marked as delivered.").build());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> read(@PathVariable Long id) {
        messageService.markRead(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Message marked as read.").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Message deleted successfully.").build());
    }
}