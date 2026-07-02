package com.chatapp.service;

import com.chatapp.data.entity.ChatRoom;
import com.chatapp.data.entity.GroupChat;
import com.chatapp.data.entity.Message;
import com.chatapp.data.entity.User;
import com.chatapp.data.repository.ChatRoomRepository;
import com.chatapp.data.repository.GroupChatRepository;
import com.chatapp.data.repository.MessageRepository;
import com.chatapp.data.repository.UserRepository;
import com.chatapp.data.repository.GroupMemberRepository;
import com.chatapp.dto.request.GroupRequest;
import com.chatapp.dto.response.ChatRoomResponse;
import com.chatapp.enums.RoomType;
import com.chatapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * ADDITIONS to support the fixed ChatController (Bug 5 & Bug 6):
 *
 *  createPrivateChat(User, User)       — resolves or creates a private room between two users
 *  createGroupRoom(User, String)       — creates the ChatRoom record for a new group
 *  createGroupWithMembers(...)         — creates GroupChat + adds all member users
 *  getAllChatRoomsForUser(User, ...)    — returns ChatRoomResponse list for the sidebar
 *  getChatRoomResponse(Long, ...)      — returns a single ChatRoomResponse by room ID
 *
 * The existing methods (createPrivateChat(Long,String), createGroupChat(Long,String),
 * getChatRoom, getAllChatRooms, updateRoomName, deleteChatRoom) are preserved unchanged
 * so nothing else in the codebase breaks.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final GroupChatRepository groupChatRepository;
    private final MessageRepository messageRepository;
    private final GroupMemberRepository groupMemberRepository;

    // -------------------------------------------------------------------------
    // EXISTING methods (kept unchanged)
    // -------------------------------------------------------------------------

    public ChatRoom createPrivateChat(Long createdById, String roomName) {

        userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setRoomType(RoomType.PRIVATE);
        room.setCreatedBy(createdById);

        return chatRoomRepository.save(room);
    }

    public ChatRoom createGroupChat(Long createdById, String roomName) {

        userRepository.findById(createdById)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setRoomType(RoomType.GROUP);
        room.setCreatedBy(createdById);

        return chatRoomRepository.save(room);
    }

    public ChatRoom getChatRoom(Long roomId) {

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found."));
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom updateRoomName(Long roomId, String roomName) {

        ChatRoom room = getChatRoom(roomId);
        room.setRoomName(roomName);
        return chatRoomRepository.save(room);
    }

    public void deleteChatRoom(Long roomId) {

        ChatRoom room = getChatRoom(roomId);
        chatRoomRepository.delete(room);
    }

    // -------------------------------------------------------------------------
    // NEW: create a private chat between two User objects
    // Reuses an existing room if one already exists between the pair.
    // -------------------------------------------------------------------------
    public ChatRoom createPrivateChat(User sender, User receiver) {

        // Check both orderings of user1/user2
        return chatRoomRepository
                .findByUser1IdAndUser2IdAndRoomType(sender.getId(), receiver.getId(), RoomType.PRIVATE)
                .or(() -> chatRoomRepository
                        .findByUser1IdAndUser2IdAndRoomType(receiver.getId(), sender.getId(), RoomType.PRIVATE))
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom();
                    room.setRoomName(sender.getFirstName() + " & " + receiver.getFirstName());
                    room.setRoomType(RoomType.PRIVATE);
                    room.setCreatedBy(sender.getId());
                    room.setUser1Id(sender.getId());
                    room.setUser2Id(receiver.getId());
                    return chatRoomRepository.save(room);
                });
    }

    // -------------------------------------------------------------------------
    // NEW: create the ChatRoom record for a group (type = GROUP)
    // -------------------------------------------------------------------------
    public ChatRoom createGroupRoom(User creator, String groupName) {

        ChatRoom room = new ChatRoom();
        room.setRoomName(groupName);
        room.setRoomType(RoomType.GROUP);
        room.setCreatedBy(creator.getId());

        return chatRoomRepository.save(room);
    }

    // -------------------------------------------------------------------------
    // NEW: create GroupChat + add all members via GroupService
    // Called after createGroupRoom() so the ChatRoom ID is already known.
    // -------------------------------------------------------------------------
    public GroupChat createGroupWithMembers(
            ChatRoom room,
            User creator,
            GroupRequest request,
            GroupService groupService) {

        GroupChat group = new GroupChat();
        group.setRoomId(room.getId());
        group.setGroupName(request.getGroupName());
        group.setCreatedBy(creator.getId());

        GroupChat savedGroup = groupService.createGroup(group);

        // Add each requested member (creator is already added by GroupService.createGroup)
        if (request.getMemberIds() != null) {
            for (Long memberId : request.getMemberIds()) {
                if (!memberId.equals(creator.getId())) {
                    try {
                        groupService.addMember(savedGroup.getId(), memberId);
                    } catch (Exception ignored) {
                        // skip duplicate or not-found members gracefully
                    }
                }
            }
        }

        return savedGroup;
    }

    // -------------------------------------------------------------------------
    // NEW: build ChatRoomResponse list for the current user's sidebar
    // For PRIVATE rooms  → name = the other person's full name
    // For GROUP rooms    → name = the GroupChat's groupName
    // -------------------------------------------------------------------------
    public List<ChatRoomResponse> getAllChatRoomsForUser(User currentUser, UserService userService) {

        List<ChatRoom> rooms = chatRoomRepository.findAll();
        List<ChatRoomResponse> responses = new ArrayList<>();

        for (ChatRoom room : rooms) {

            // Only include rooms this user is part of
            if (!isUserInRoom(room, currentUser.getId())) {
                continue;
            }

            Optional<Message> lastMessageOptional =
                    messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(room.getId());

            String lastMessage;
            LocalDateTime lastMessageTime;
            Long lastSenderId;
            String lastSenderName = null;

            if (lastMessageOptional.isPresent()) {

                Message message = lastMessageOptional.get();

                lastMessage = message.getMessage();
                lastMessageTime = message.getCreatedAt();
                lastSenderId = message.getSenderId();

                try {
                    User sender = userService.getUser(message.getSenderId());
                    lastSenderName = sender.getFirstName();
                } catch (Exception ignored) {
                }
            } else {
                lastSenderId = null;
                lastMessageTime = null;
                lastMessage = null;
            }

            if (room.getRoomType() == RoomType.PRIVATE) {

                Long otherUserId = room.getUser1Id().equals(currentUser.getId())
                        ? room.getUser2Id()
                        : room.getUser1Id();

                try {
                    User other = userService.getUser(otherUserId);
                    responses.add(ChatRoomResponse.builder()
                            .id(room.getId())
                            .name(other.getFirstName() + " " + other.getLastName())
                            .roomType(room.getRoomType().name())
                            .receiverId(other.getId())
                            .profilePicture(other.getProfilePicture())
                            .online(other.getOnline())
                            .lastMessage(lastMessage)
                            .lastMessageTime(lastMessageTime)
                            .lastSenderId(lastSenderId)
                            .lastSenderName(lastSenderName)
                            .build());
                } catch (Exception ignored) {
                    // other user deleted — skip this room
                }

            } else {

                // GROUP room — find the GroupChat record
                String finalLastSenderName = lastSenderName;
                groupChatRepository.findByRoomId(room.getId()).ifPresent(group ->
                        responses.add(ChatRoomResponse.builder()
                                .id(room.getId())
                                .name(group.getGroupName())
                                .roomType(room.getRoomType().name())
                                .groupId(group.getId())
                                .profilePicture(group.getGroupImage())
                                .online(false)
                                .lastMessage(lastMessage)
                                .lastMessageTime(lastMessageTime)
                                .lastSenderId(lastSenderId)
                                .lastSenderName(finalLastSenderName)
                                .build())
                );
            }
        }

        return responses;
    }

    // -------------------------------------------------------------------------
    // NEW: get a single ChatRoomResponse by room ID for the current user
    // -------------------------------------------------------------------------
    public ChatRoomResponse getChatRoomResponse(Long roomId, User currentUser, UserService userService) {

        ChatRoom room = getChatRoom(roomId);

        if (room.getRoomType() == RoomType.PRIVATE) {

            Long otherUserId = room.getUser1Id().equals(currentUser.getId())
                    ? room.getUser2Id()
                    : room.getUser1Id();

            User other = userService.getUser(otherUserId);

            return ChatRoomResponse.builder()
                    .id(room.getId())
                    .name(other.getFirstName() + " " + other.getLastName())
                    .roomType(room.getRoomType().name())
                    .receiverId(other.getId())
                    .profilePicture(other.getProfilePicture())
                    .online(other.getOnline())
                    .build();

        } else {

            GroupChat group = groupChatRepository.findByRoomId(room.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found for room " + roomId));

            return ChatRoomResponse.builder()
                    .id(room.getId())
                    .name(group.getGroupName())
                    .roomType(room.getRoomType().name())
                    .groupId(group.getId())
                    .profilePicture(group.getGroupImage())
                    .online(false)
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // Helper: is this user a participant in the given room?
    // -------------------------------------------------------------------------
    private boolean isUserInRoom(ChatRoom room, Long userId) {
        if (room.getRoomType() == RoomType.PRIVATE) {
            return userId.equals(room.getUser1Id()) || userId.equals(room.getUser2Id());
        }
        // For GROUP rooms, membership check is in GroupMemberRepository.
        // For now, return true and let the group lookup fail gracefully for non-members.
        return groupChatRepository.findByRoomId(room.getId())
                .map(group -> groupMemberRepository.existsByGroupIdAndUserId(group.getId(), userId))
                .orElse(false);
    }
}