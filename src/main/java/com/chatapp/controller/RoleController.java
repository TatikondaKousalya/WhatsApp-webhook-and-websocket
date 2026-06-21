package com.chatapp.controller;

import com.chatapp.data.entity.Role;
import com.chatapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public Role create(@RequestBody Role role){
        return roleService.createRole(role);
    }



    @GetMapping
    public List<Role> all(){
        return roleService.getAllRoles();
    }



    @GetMapping("/{id}")
    public Role get(@PathVariable Long id){
        return roleService.getRole(id);
    }



    @PutMapping("/{id}")
    public Role update(@PathVariable Long id, @RequestBody Role role){
        return roleService.updateRole(id,role);
    }



    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        roleService.deleteRole(id);
        return "Deleted";
    }

}