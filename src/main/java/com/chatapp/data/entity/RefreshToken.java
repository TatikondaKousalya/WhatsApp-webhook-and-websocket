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
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean revoked = false;
}