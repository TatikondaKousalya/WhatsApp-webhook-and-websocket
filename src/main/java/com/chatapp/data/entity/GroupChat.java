package com.chatapp.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "group_chat")
public class GroupChat extends BaseEntity {

    @Column(name = "room_id", nullable = false, unique = true)
    private Long roomId;

    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Column(name = "group_image")
    private String groupImage;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by", nullable =false)
    private Long createdBy;
}