package com.chatapp.service;

import com.chatapp.data.entity.ChatRoom;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.ChatRoomRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.enums.RoomType;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    /**
     * Create Private Chat
     */
    public ChatRoom createPrivateChat(Long createdById, String roomName) {

        User user = userRepository.findById(createdById)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setRoomType(RoomType.PRIVATE);
        room.setCreatedBy(user);

        return chatRoomRepository.save(room);
    }

    /**
     * Create Group Chat
     */
    public ChatRoom createGroupChat(Long createdById, String roomName) {

        User user = userRepository.findById(createdById)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setRoomType(RoomType.GROUP);
        room.setCreatedBy(user);

        return chatRoomRepository.save(room);
    }

    /**
     * Get Chat Room
     */
    public ChatRoom getChatRoom(Long roomId) {

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Chat room not found."));
    }

    /**
     * Get All Chat Rooms
     */
    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    /**
     * Update Chat Name
     */
    public ChatRoom updateRoomName(Long roomId, String roomName) {

        ChatRoom room = getChatRoom(roomId);

        room.setRoomName(roomName);

        return chatRoomRepository.save(room);
    }

    /**
     * Delete Chat Room
     */
    public void deleteChatRoom(Long roomId) {

        ChatRoom room = getChatRoom(roomId);

        chatRoomRepository.delete(room);
    }
}