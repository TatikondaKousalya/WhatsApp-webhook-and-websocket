package com.chatapp.controller;

import com.chatapp.data.entity.GroupChat;
import com.chatapp.data.entity.GroupMember;
import com.chatapp.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public GroupChat create(@RequestBody GroupChat group){
        return groupService.createGroup(group);
    }

    @PostMapping("/{groupId}/members/{userId}")
    public String add(@PathVariable Long groupId, @PathVariable Long userId){
        groupService.addMember(groupId,userId);
        return "Member Added";
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public String remove(@PathVariable Long groupId, @PathVariable Long userId){
        groupService.removeMember(groupId,userId);
        return "Member Removed";
    }

    @GetMapping("/{id}")
    public GroupChat get(@PathVariable Long id){
        return groupService.getGroup(id);
    }

    @GetMapping("/{id}/members")
    public List<GroupMember> members(@PathVariable Long id){
        return groupService.getMembers(id);
    }

    @PutMapping
    public GroupChat update(@RequestBody GroupChat group){
        return groupService.updateGroup(group);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        groupService.deleteGroup(id);
        return "Group Deleted";
    }

}