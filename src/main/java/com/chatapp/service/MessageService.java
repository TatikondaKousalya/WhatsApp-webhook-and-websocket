package com.chatapp.service;

import com.chatapp.data.entity.*;
import com.chatapp.data.repository.*;
import com.chatapp.enums.MessageStatus;
import com.chatapp.enums.MessageType;
import com.chatapp.enums.RoomType;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GroupChatRepository groupChatRepository;
    private final AttachmentRepository attachmentRepository;

    // Send Private Message
    public Message sendPrivateMessage(Long senderId, Long receiverId,
                                      String text, Long attachmentId) {

        // Validate sender
        userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));

        // Validate receiver
        userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));

        // Find existing private chat
        ChatRoom room = chatRoomRepository
                .findByUser1IdAndUser2IdAndRoomType(senderId, receiverId, RoomType.PRIVATE)
                .or(() -> chatRoomRepository.findByUser1IdAndUser2IdAndRoomType(receiverId, senderId, RoomType.PRIVATE))
                .orElseGet(() -> createChatRoom(senderId, receiverId));

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setChatRoomId(room.getId());
        message.setMessage(text);
        message.setMessageStatus(MessageStatus.SENT);

        if (attachmentId != null) {

            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attachment not found."));

            message.setAttachment(attachment.getFileUrl());

            String fileType = attachment.getFileType();

            if (fileType.startsWith("image/")) {
                message.setMessageType(MessageType.IMAGE);
            } else if (fileType.startsWith("video/")) {
                message.setMessageType(MessageType.VIDEO);
            } else if (fileType.startsWith("audio/")) {
                message.setMessageType(MessageType.AUDIO);
            } else {
                message.setMessageType(MessageType.FILE);
            }

        } else {
            message.setMessageType(MessageType.TEXT);
        }

        return messageRepository.save(message);
    }

    private ChatRoom createChatRoom(Long senderId, Long receiverId) {

        ChatRoom room = new ChatRoom();
        room.setRoomName("Private Chat");
        room.setRoomType(RoomType.PRIVATE);
        room.setCreatedBy(senderId);
        room.setUser1Id(senderId);
        room.setUser2Id(receiverId);

        return chatRoomRepository.save(room);
    }


    // Send Group Message
    public Message sendGroupMessage(Long senderId, Long groupId,
                                    String text, Long attachmentId) {

        // Validate sender
        userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Validate group
        GroupChat group = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found."));

        Message message = new Message();
        message.setSenderId(senderId);
        message.setGroupId(groupId);
        message.setChatRoomId(group.getRoomId());   // Group belongs to a chat room
        message.setMessage(text);
        message.setMessageStatus(MessageStatus.SENT);

        if (attachmentId != null) {

            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attachment not found."));

            message.setAttachment(attachment.getFileUrl());

            String fileType = attachment.getFileType();

            if (fileType.startsWith("image/")) {
                message.setMessageType(MessageType.IMAGE);
            } else if (fileType.startsWith("video/")) {
                message.setMessageType(MessageType.VIDEO);
            } else if (fileType.startsWith("audio/")) {
                message.setMessageType(MessageType.AUDIO);
            } else {
                message.setMessageType(MessageType.FILE);
            }

        } else {
            message.setMessageType(MessageType.TEXT);
        }

        return messageRepository.save(message);
    }

    // Get private chat history
    public List<Message> getPrivateMessages(Long roomId) {
        return messageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
    }

    // Get group messages
    public List<Message> getGroupMessages(Long groupId) {
        return messageRepository.findByGroupIdOrderByCreatedAtAsc(groupId);
    }

    // Mark delivered
    public void markDelivered(Long messageId) {

        Message message = getMessage(messageId);
        message.setMessageStatus(MessageStatus.DELIVERED);
        messageRepository.save(message);
    }

    // Mark read
    public void markRead(Long messageId) {

        Message message = getMessage(messageId);
        message.setMessageStatus(MessageStatus.READ);
        messageRepository.save(message);
    }

    // Delete message
    public void deleteMessage(Long messageId) {

        messageRepository.delete(getMessage(messageId));
    }

    // Get single message
    public Message getMessage(Long messageId) {

        return messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found."));
    }
}