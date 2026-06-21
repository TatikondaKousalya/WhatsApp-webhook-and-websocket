package com.chatapp.controller;

import com.chatapp.data.entity.Message;
import com.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // Send private message
    @PostMapping("/private")
    public Message privateMessage(@RequestParam Long senderId, @RequestParam Long receiverId,
            @RequestParam String text, @RequestParam(required = false) Long attachmentId) {

        return messageService.sendPrivateMessage(senderId, receiverId, text, attachmentId
        );
    }

    // Send group message
    @PostMapping("/group")
    public Message groupMessage(@RequestParam Long senderId, @RequestParam Long groupId,
            @RequestParam String text, @RequestParam(required = false) Long attachmentId) {
        return messageService.sendGroupMessage(senderId, groupId, text, attachmentId
        );
    }


    // private history
    @GetMapping("/private/{roomId}")
    public List<Message> privateHistory(@PathVariable Long roomId){
        return messageService.getPrivateMessages(roomId);
    }

    // group history
    @GetMapping("/group/{groupId}")
    public List<Message> groupHistory(@PathVariable Long groupId){
        return messageService.getGroupMessages(groupId);
    }

    @PutMapping("/{id}/delivered")
    public String delivered(@PathVariable Long id){
        messageService.markDelivered(id);
        return "Delivered";
    }

    @PutMapping("/{id}/read")
    public String read(@PathVariable Long id){
        messageService.markRead(id);
        return "Read";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        messageService.deleteMessage(id);
        return "Deleted";
    }
}