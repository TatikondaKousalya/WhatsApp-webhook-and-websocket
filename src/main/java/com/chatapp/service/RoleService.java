package com.chatapp.service;

import com.chatapp.data.entity.Role;
import com.chatapp.data.repository.RoleRepository;
import com.chatapp.exception.BadRequestException;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    //Create Role
    public Role createRole(Role role) {

        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new BadRequestException("Role already exists.");
        }
        return roleRepository.save(role);
    }

    // Get Role By Id
    public Role getRole(Long roleId) {

        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }

    // Get Role By Name
    public Role getRoleByName(String name) {

        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }

    //Get All Roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    //Update Role
    public Role updateRole(Long roleId, Role request) {

        Role role = getRole(roleId);
        role.setName(request.getName());
        return roleRepository.save(role);
    }


    // Delete Role
    public void deleteRole(Long roleId) {

        Role role = getRole(roleId);
        roleRepository.delete(role);
    }
}