package com.chatapp.controller;

import com.chatapp.data.entity.Role;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Role>builder().success(true)
                        .message("Role created successfully.").data(createdRole).build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> all() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.<List<Role>>builder()
                .success(true).message("Roles fetched successfully.")
                .data(roles).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> get(@PathVariable Long id) {
        Role role = roleService.getRole(id);
        return ResponseEntity.ok(ApiResponse.<Role>builder()
                .success(true).message("Role fetched successfully.")
                .data(role).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> update(@PathVariable Long id, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        return ResponseEntity.ok(ApiResponse.<Role>builder()
                .success(true).message("Role updated successfully.")
                .data(updatedRole).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true).message("Role deleted successfully.").build());
    }

}