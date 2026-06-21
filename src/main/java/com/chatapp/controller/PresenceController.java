package com.chatapp.controller;

import com.chatapp.data.entity.UserPresence;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @PostMapping("/{id}/online")
    public ResponseEntity<ApiResponse<Void>> online(@PathVariable Long id, @RequestParam String session) {
        presenceService.markOnline(id, session);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("User marked online.").build()
        );
    }

    @PostMapping("/{id}/offline")
    public ResponseEntity<ApiResponse<Void>> offline(@PathVariable Long id) {
        presenceService.markOffline(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("User marked offline.").build());
    }

    @PostMapping("/{id}/away")
    public ResponseEntity<ApiResponse<Void>> away(@PathVariable Long id) {
        presenceService.markAway(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("User marked away.").build());
    }

    @PostMapping("/{id}/busy")
    public ResponseEntity<ApiResponse<Void>> busy(@PathVariable Long id) {
        presenceService.markBusy(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("User marked busy.").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserPresence>> status(@PathVariable Long id) {
        UserPresence presence = presenceService.getPresence(id);
        return ResponseEntity.ok(ApiResponse.<UserPresence>builder()
                .success(true).message("User presence fetched successfully.")
                .data(presence).build());
    }

}