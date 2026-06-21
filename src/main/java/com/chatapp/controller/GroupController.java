package com.chatapp.controller;

import com.chatapp.data.entity.GroupChat;
import com.chatapp.data.entity.GroupMember;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupChat>> create(@RequestBody GroupChat group) {
        GroupChat createdGroup = groupService.createGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<GroupChat>builder()
                .success(true).message("Group created successfully.")
                .data(createdGroup).build());
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> add(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addMember(groupId, userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Member added successfully.").build());
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> remove(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeMember(groupId, userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Member removed successfully.").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupChat>> get(@PathVariable Long id) {
        GroupChat group = groupService.getGroup(id);
        return ResponseEntity.ok(ApiResponse.<GroupChat>builder()
                .success(true).message("Group fetched successfully.")
                .data(group).build());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<GroupMember>>> members(@PathVariable Long id) {
        List<GroupMember> members = groupService.getMembers(id);
        return ResponseEntity.ok(ApiResponse.<List<GroupMember>>builder()
                .success(true).message("Group members fetched successfully.")
                .data(members).build());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<GroupChat>> update(@RequestBody GroupChat group) {
        GroupChat updatedGroup = groupService.updateGroup(group);
        return ResponseEntity.ok(ApiResponse.<GroupChat>builder()
                .success(true).message("Group updated successfully.")
                .data(updatedGroup).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Group deleted successfully.").build());
    }
}