package com.chatapp.data.entity;

import com.chatapp.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_presence")
public class UserPresence extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "socket_session")
    private String socketSession;
}