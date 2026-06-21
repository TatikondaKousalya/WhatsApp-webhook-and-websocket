package com.chatapp.controller;

import com.chatapp.dto.request.*;
import com.chatapp.dto.response.AuthResponse;
import com.chatapp.dto.response.UserResponse;
import com.chatapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }


    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }


    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }


    @PostMapping("/logout")
    public String logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return "Logout successful";
    }


    @PutMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return "Password changed";
    }


    @GetMapping("/me")
    public UserResponse currentUser(){
        return authService.getCurrentUser();
    }
}