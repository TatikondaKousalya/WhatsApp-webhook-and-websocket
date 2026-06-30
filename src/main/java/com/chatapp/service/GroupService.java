package com.chatapp.service;

import com.chatapp.data.entity.GroupChat;
import com.chatapp.data.entity.GroupMember;
import com.chatapp.data.repository.ChatRoomRepository;
import com.chatapp.data.repository.GroupChatRepository;
import com.chatapp.data.repository.GroupMemberRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.dto.response.GroupMemberProjection;
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
    private final ChatRoomRepository chatRoomRepository;

    // Create Group
    public GroupChat createGroup(GroupChat group) {

        // Validate creator
        userRepository.findById(group.getCreatedBy())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Validate chat room
        chatRoomRepository.findById(group.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Chat room not found."));

        GroupChat savedGroup = groupChatRepository.save(group);

        GroupMember member = new GroupMember();
        member.setGroupId(savedGroup.getId());
        member.setUserId(group.getCreatedBy());
        member.setAdmin(true);
        member.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(member);

        return savedGroup;
    }

    // Add Member
    public void addMember(Long groupId, Long userId) {

        // Validate group
        groupChatRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found."));

        // Validate user
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Check if already a member
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new BadRequestException("User already exists in group.");
        }

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
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
    public List<GroupMemberProjection> getMembers(Long groupId) {
        return groupMemberRepository.findMembersWithUsername(groupId);
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