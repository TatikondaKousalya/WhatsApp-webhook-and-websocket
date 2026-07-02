package com.chatapp.data.repository;

import com.chatapp.data.entity.GroupMember;
import com.chatapp.dto.response.GroupMemberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroupId(Long groupId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    @Query(value = "select " +
            "            gm.user_id as userId, " +
            "            u.username as username, " +
            "            gm.is_admin as admin, " +
            "            gm.joined_at as joinedAt, " +
            "            u.profile_picture as profileImage " +
            "       from " +
            "             group_members gm join users u on gm.user_id = u.id " +
            "       where " +
            "              gm.group_id = :groupId ", nativeQuery = true)
    List<GroupMemberProjection> findMembersWithUsername(Long groupId);
}