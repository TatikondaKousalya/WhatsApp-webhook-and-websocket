package com.chatapp.controller;

import com.chatapp.data.entity.ChatRoom;
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
    public ChatRoom privateChat(Long userId, String name){
        return chatService.createPrivateChat(userId,name);
    }

    @PostMapping("/group")
    public ChatRoom groupChat(Long userId, String name){
        return chatService.createGroupChat(userId,name);
    }

    @GetMapping
    public List<ChatRoom> all(){
        return chatService.getAllChatRooms();
    }

    @GetMapping("/{id}")
    public ChatRoom get(@PathVariable Long id){
        return chatService.getChatRoom(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        chatService.deleteChatRoom(id);
    }

}