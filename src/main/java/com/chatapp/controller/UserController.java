package com.chatapp.controller;

import com.chatapp.data.entity.User;
import com.chatapp.dto.request.UpdateUserRequest;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true).message("User fetched successfully.")
                .data(user).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> users() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                .success(true).message("Users fetched successfully.")
                .data(users).build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> me() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true).message("Current user fetched successfully.")
                .data(user).build());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<User>> update(@RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.<User>builder()
                .success(true).message("Profile updated successfully.")
                .data(updatedUser).build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> search(@RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(ApiResponse.<List<User>>builder()
                .success(true).message("Search completed successfully.")
                .data(users).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("User deleted successfully.").build());
    }
}