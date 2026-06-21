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

    //Send private message
    public Message sendPrivateMessage(Long senderId, Long receiverId,
                                      String text, Long attachmentId) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));

        ChatRoom room = chatRoomRepository
                .findByUser1AndUser2AndRoomType(sender, receiver, RoomType.PRIVATE)
                .or(() -> chatRoomRepository.findByUser1AndUser2AndRoomType(receiver, sender, RoomType.PRIVATE))
                .orElseGet(() -> createChatRoom(sender, receiver));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setChatRoom(room);
        message.setMessage(text);
        message.setMessageStatus(MessageStatus.SENT);

        if (attachmentId != null) {

            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attachment not found."));

            message.setAttachment(attachment.getFilePath());
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

    private ChatRoom createChatRoom(User sender, User receiver) {

        ChatRoom room = new ChatRoom();

        room.setRoomName(sender.getUsername() + "_" + receiver.getUsername());
        room.setRoomType(RoomType.PRIVATE);
        room.setCreatedBy(sender);
        room.setUser1(sender);
        room.setUser2(receiver);

        return chatRoomRepository.save(room);
    }


    // Send group message
    public Message sendGroupMessage(Long senderId, Long groupId,
                                    String text, Long attachmentId) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        GroupChat group = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found."));

        Message message = new Message();

        message.setSender(sender);
        message.setGroup(group);
        message.setMessage(text);
        message.setMessageStatus(MessageStatus.SENT);

        if (attachmentId != null) {

            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attachment not found."));

            message.setAttachment(attachment.getFilePath());
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