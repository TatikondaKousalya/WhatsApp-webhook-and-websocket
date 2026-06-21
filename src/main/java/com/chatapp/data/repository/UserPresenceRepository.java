package com.chatapp.data.repository;

import com.chatapp.data.entity.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserPresenceRepository extends JpaRepository<UserPresence, Long> {
    Optional<UserPresence> findByUserId(Long userId);
}
