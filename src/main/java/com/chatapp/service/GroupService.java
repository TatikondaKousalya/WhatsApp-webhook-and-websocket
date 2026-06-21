package com.chatapp.service;

import com.chatapp.data.entity.GroupChat;
import com.chatapp.data.entity.GroupMember;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.GroupChatRepository;
import com.chatapp.data.repository.GroupMemberRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.exception.BadRequestException;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupChatRepository groupChatRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    // Create Group
    public GroupChat createGroup(GroupChat group) {

        User creator = userRepository.findById(group.getCreatedBy().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        group.setCreatedBy(creator);

        GroupChat savedGroup = groupChatRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroup(savedGroup);
        member.setUser(creator);
        member.setAdmin(true);
        member.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(member);

        return savedGroup;
    }

    // Add Member
    public void addMember(Long groupId, Long userId) {

        GroupChat group = groupChatRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BadRequestException("User already exists in group.");
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setAdmin(false);
        member.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(member);
    }

    // Remove Member
    public void removeMember(Long groupId, Long userId) {

        GroupMember member = groupMemberRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Member not found."));

        groupMemberRepository.delete(member);
    }

    // Get Group
    public GroupChat getGroup(Long groupId) {

        return groupChatRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found."));
    }

    // Get Members
    public List<GroupMember> getMembers(Long groupId) {

        return groupMemberRepository.findByGroupId(groupId);
    }

    // Update Group
    public GroupChat updateGroup(GroupChat request) {

        GroupChat group = getGroup(request.getId());

        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        group.setGroupImage(request.getGroupImage());

        return groupChatRepository.save(group);
    }

    // Delete Group
    public void deleteGroup(Long groupId) {

        GroupChat group = getGroup(groupId);

        groupChatRepository.delete(group);
    }

    // Leave Group
    public void leaveGroup(Long groupId, Long userId) {

        removeMember(groupId, userId);
    }
}