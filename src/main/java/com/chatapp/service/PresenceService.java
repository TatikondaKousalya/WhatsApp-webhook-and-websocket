package com.chatapp.service;

import com.chatapp.data.entity.User;
import com.chatapp.data.entity.UserPresence;
import com.chatapp.data.repository.UserPresenceRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.enums.UserStatus;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final UserRepository userRepository;
    private final UserPresenceRepository userPresenceRepository;

    public void markOnline(Long userId, String sessionId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        UserPresence presence = userPresenceRepository.findByUserId(userId)
                .orElse(new UserPresence());

        presence.setUser(user);
        presence.setStatus(UserStatus.ONLINE);
        presence.setLastSeen(LocalDateTime.now());
        presence.setSocketSession(sessionId);

        userPresenceRepository.save(presence);
    }

    public void markOffline(Long userId) {

        UserPresence presence = userPresenceRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presence not found."));

        presence.setStatus(UserStatus.OFFLINE);
        presence.setLastSeen(LocalDateTime.now());
        presence.setSocketSession(null);

        userPresenceRepository.save(presence);
    }

    public void markAway(Long userId) {

        UserPresence presence = userPresenceRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presence not found."));

        presence.setStatus(UserStatus.AWAY);

        userPresenceRepository.save(presence);
    }

    public void markBusy(Long userId) {

        UserPresence presence = userPresenceRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presence not found."));

        presence.setStatus(UserStatus.BUSY);

        userPresenceRepository.save(presence);
    }

    public UserPresence getPresence(Long userId) {

        return userPresenceRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Presence not found."));
    }
}