package com.chatapp.controller;

import com.chatapp.data.entity.ChatRoom;
import com.chatapp.data.entity.GroupChat;
import com.chatapp.data.entity.User;
import com.chatapp.dto.request.GroupRequest;
import com.chatapp.dto.response.ApiResponse;
import com.chatapp.dto.response.ChatRoomResponse;
import com.chatapp.service.ChatService;
import com.chatapp.service.GroupService;
import com.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * FIXES applied to this controller:
 *
 * FIX (Bug 5a) — createPrivateChat:
 *   Old: @RequestParam Long userId, @RequestParam String name
 *   Frontend sends: POST /chats/private  body = { receiverId: <Long> }
 *   The old signature expected query params and took a "name" that the frontend
 *   never sends. Replaced with @RequestBody carrying just receiverId.
 *   The logged-in user (sender) is resolved via UserService.getCurrentUser()
 *   instead of being passed as a param — matches how AuthService works.
 *
 * FIX (Bug 5b) — createGroupChat:
 *   Old: @RequestParam Long userId, @RequestParam String name
 *   Frontend sends: POST /chats/group  body = { name: "...", members: [id, ...] }
 *   Replaced with @RequestBody GroupRequest (which already has groupName + memberIds).
 *   Members are added via GroupService after the ChatRoom is created.
 *
 * FIX (Bug 6) — all endpoints now return ChatRoomResponse DTO instead of raw
 *   ChatRoom entity. The entity has "roomName" but the frontend reads "name";
 *   the entity is missing receiverId, groupId, profilePicture, online, lastMessage —
 *   all of which the frontend accesses. ChatRoomResponse already has the correct
 *   field names. The mapToResponse() helper fills them in.
 */
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final GroupService groupService;
    private final UserService userService;

    // -------------------------------------------------------------------------
    // POST /api/chats/private
    // Body: { "receiverId": <Long> }
    // -------------------------------------------------------------------------
    @PostMapping("/private")
    public ApiResponse<ChatRoomResponse> privateChat(@RequestBody CreatePrivateChatRequest request) {

        User currentUser = userService.getCurrentUser();
        User receiver = userService.getUser(request.getReceiverId());

        ChatRoom room = chatService.createPrivateChat(currentUser, receiver);

        return ApiResponse.<ChatRoomResponse>builder()
                .success(true)
                .message("Private chat created successfully.")
                .data(mapToPrivateResponse(room, receiver, currentUser))
                .build();
    }

    // -------------------------------------------------------------------------
    // POST /api/chats/group
    // Body: { "name": "...", "members": [id, id, ...] }
    // Frontend CreateGroupDialog sends { name, members } — mapped below.
    // -------------------------------------------------------------------------
    @PostMapping("/group")
    public ApiResponse<ChatRoomResponse> groupChat(@RequestBody CreateGroupChatRequest request) {

        User currentUser = userService.getCurrentUser();

        // 1. Create the ChatRoom (type = GROUP)
        ChatRoom room = chatService.createGroupRoom(currentUser, request.getName());

        // 2. Create the GroupChat record linked to this room
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setGroupName(request.getName());
        groupRequest.setMemberIds(request.getMembers());

        GroupChat group = chatService.createGroupWithMembers(room, currentUser, groupRequest, groupService);

        return ApiResponse.<ChatRoomResponse>builder()
                .success(true)
                .message("Group chat created successfully.")
                .data(mapToGroupResponse(room, group))
                .build();
    }

    // -------------------------------------------------------------------------
    // GET /api/chats
    // -------------------------------------------------------------------------
    @GetMapping
    public ApiResponse<List<ChatRoomResponse>> all() {

        User currentUser = userService.getCurrentUser();

        List<ChatRoomResponse> responses = chatService.getAllChatRoomsForUser(currentUser, userService);

        return ApiResponse.<List<ChatRoomResponse>>builder()
                .success(true)
                .message("Chat rooms fetched successfully.")
                .data(responses)
                .build();
    }

    // -------------------------------------------------------------------------
    // GET /api/chats/{id}
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    public ApiResponse<ChatRoomResponse> get(@PathVariable Long id) {

        User currentUser = userService.getCurrentUser();
        ChatRoomResponse response = chatService.getChatRoomResponse(id, currentUser, userService);

        return ApiResponse.<ChatRoomResponse>builder()
                .success(true)
                .message("Chat room fetched successfully.")
                .data(response)
                .build();
    }

    // -------------------------------------------------------------------------
    // DELETE /api/chats/{id}
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {

        chatService.deleteChatRoom(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Chat room deleted successfully.")
                .build();
    }

    // -------------------------------------------------------------------------
    // Helper: map a private ChatRoom to ChatRoomResponse
    // "name" = the other person's full name (what the frontend displays)
    // "receiverId" = the other person's ID (used by MessageWindow to send messages)
    // -------------------------------------------------------------------------
    private ChatRoomResponse mapToPrivateResponse(ChatRoom room, User otherUser, User currentUser) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .name(otherUser.getFirstName() + " " + otherUser.getLastName())
                .roomType(room.getRoomType().name())
                .receiverId(otherUser.getId())
                .profilePicture(otherUser.getProfilePicture())
                .online(otherUser.getOnline())
                .build();
    }

    // Helper: map a group ChatRoom + GroupChat to ChatRoomResponse
    private ChatRoomResponse mapToGroupResponse(ChatRoom room, GroupChat group) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .name(group.getGroupName())
                .roomType(room.getRoomType().name())
                .groupId(group.getId())
                .profilePicture(group.getGroupImage())
                .online(false)
                .build();
    }

    // -------------------------------------------------------------------------
    // Inner request DTOs (kept here to avoid new files for minimal change scope)
    // -------------------------------------------------------------------------

    /**
     * Body for POST /api/chats/private
     * Frontend sends: { "receiverId": 5 }
     */
    @lombok.Data
    public static class CreatePrivateChatRequest {
        private Long receiverId;
    }

    /**
     * Body for POST /api/chats/group
     * Frontend CreateGroupDialog sends: { "name": "Team Chat", "members": [2, 3, 4] }
     */
    @lombok.Data
    public static class CreateGroupChatRequest {
        private String name;
        private List<Long> members;
    }
}