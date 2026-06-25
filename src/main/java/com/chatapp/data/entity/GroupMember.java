package com.chatapp.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "group_members")
public class GroupMember extends BaseEntity {

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_admin", nullable = false)
    private Boolean admin = false;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}