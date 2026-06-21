package com.chatapp.controller;

import com.chatapp.data.entity.ChatRoom;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/private")
    public ApiResponse<ChatRoom> privateChat(@RequestParam Long userId, @RequestParam String name) {
        return ApiResponse.<ChatRoom>builder().success(true)
                .message("Private chat created successfully.")
                .data(chatService.createPrivateChat(userId, name)).build();
    }

    @PostMapping("/group")
    public ApiResponse<ChatRoom> groupChat(@RequestParam Long userId, @RequestParam String name) {
        return ApiResponse.<ChatRoom>builder().success(true)
                .message("Group chat created successfully.")
                .data(chatService.createGroupChat(userId, name)).build();
    }


    @GetMapping
    public ApiResponse<List<ChatRoom>> all() {
        return ApiResponse.<List<ChatRoom>>builder().success(true)
                .message("Chat rooms fetched successfully.")
                .data(chatService.getAllChatRooms()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ChatRoom> get(@PathVariable Long id) {
        return ApiResponse.<ChatRoom>builder().success(true)
                .message("Chat room fetched successfully.")
                .data(chatService.getChatRoom(id)).build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        chatService.deleteChatRoom(id);
        return ApiResponse.<Void>builder().success(true)
                .message("Chat room deleted successfully.").build();
    }
}