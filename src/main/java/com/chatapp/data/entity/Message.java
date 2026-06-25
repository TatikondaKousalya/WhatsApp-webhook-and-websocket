package com.chatapp.data.entity;

import com.chatapp.enums.MessageStatus;
import com.chatapp.enums.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_status", nullable = false)
    private MessageStatus messageStatus;

    @Column(name = "attachment")
    private String attachment;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
}