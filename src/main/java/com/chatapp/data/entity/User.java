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
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(nullable = false)
    private Boolean online = false;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "role_id", nullable = false)
    private Long roleId;
}