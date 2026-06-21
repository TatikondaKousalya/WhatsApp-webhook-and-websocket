package com.chatapp.service;

import com.chatapp.data.entity.RefreshToken;
import com.chatapp.data.entity.Role;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.RoleRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.dto.request.ChangePasswordRequest;
import com.chatapp.dto.request.LoginRequest;
import com.chatapp.dto.request.RefreshTokenRequest;
import com.chatapp.dto.request.RegisterRequest;
import com.chatapp.dto.response.AuthResponse;
import com.chatapp.dto.response.UserResponse;
import com.chatapp.exception.BadRequestException;
import com.chatapp.exception.InvalidTokenException;
import com.chatapp.exception.ResourceNotFoundException;
import com.chatapp.exception.UserAlreadyExistsException;
import com.chatapp.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${application.security.jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Register User
     */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered.");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException("Phone number already registered.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match.");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found."));

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        /*
         * Username generation
         * Example:
         * John + 1023 -> john1023
         */

        String username = (request.getFirstName() + request.getLastName())
                .replaceAll("\\s+", "").toLowerCase();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setDeleted(false);
        user.setOnline(false);
        user.setLastSeen(LocalDateTime.now());
        user.setProfilePicture("default-profile.png");
        user.setRole(role);

        User savedUser = userRepository.save(user);
        String accessToken = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return AuthResponse.builder().accessToken(accessToken)
                .refreshToken(refreshToken.getToken()).tokenType("Bearer")
                .expiresIn(jwtExpiration).user(mapUser(savedUser)).build();
    }

    private UserResponse mapUser(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfilePicture())
                .bio(user.getBio())
                .online(user.getOnline())
                .lastSeen(user.getLastSeen())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Login User
     */
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid email or password.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.setOnline(true);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder().accessToken(accessToken)
                .refreshToken(refreshToken.getToken()).tokenType("Bearer")
                .expiresIn(jwtExpiration).user(mapUser(user)).build();
    }
    /**
     * Generate new access token using refresh token
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder().accessToken(accessToken)
                .refreshToken(refreshToken.getToken()).tokenType("Bearer")
                .expiresIn(jwtExpiration).user(mapUser(user)).build();
    }

    /**
     * Logout User
     */
    public void logout(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh token is required.");
        }

        User user = getCurrentUserEntity();
        user.setOnline(false);
        user.setLastSeen(LocalDateTime.now());

        userRepository.save(user);
        refreshTokenService.revokeRefreshToken(refreshToken);
        SecurityContextHolder.clearContext();
    }

    /**
     * Change Password
     */
    public void changePassword(ChangePasswordRequest request) {

        User user = getCurrentUserEntity();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BadRequestException("New password cannot be the same as the current password.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Get Logged-in User
     */
    public UserResponse getCurrentUser() {
        return mapUser(getCurrentUserEntity());
    }

    /**
     * Returns logged-in User entity
     */
    private User getCurrentUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("User not found."));
    }
}