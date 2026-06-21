package com.chatapp.controller;

import com.chatapp.data.entity.User;
import com.chatapp.dto.request.UpdateUserRequest;
import com.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id){
        return userService.getUser(id);
    }


    @GetMapping
    public List<User> users(){
        return userService.getAllUsers();
    }


    @GetMapping("/me")
    public User me(){
        return userService.getCurrentUser();
    }


    @PutMapping
    public User update(@RequestBody UpdateUserRequest request){
        return userService.updateProfile(request);
    }


    @GetMapping("/search")
    public List<User> search(@RequestParam String keyword){
        return userService.searchUsers(keyword);
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        userService.deleteUser(id);
        return "User deleted";
    }
}