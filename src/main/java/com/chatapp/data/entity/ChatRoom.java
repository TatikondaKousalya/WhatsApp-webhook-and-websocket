package com.chatapp.data.entity;

import com.chatapp.enums.RoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "user1_id")
    private Long user1Id;

    @Column(name = "user2_id")
    private Long user2Id;
}