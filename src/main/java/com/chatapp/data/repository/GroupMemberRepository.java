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
    @Query("""
            SELECT
                gm.userId AS userId,
                u.username AS username,
                gm.admin AS admin,
                gm.joinedAt AS joinedAt
            FROM GroupMember gm
            JOIN User u
            ON gm.userId = u.id
            WHERE gm.groupId = :groupId
""")
    List<GroupMemberProjection> findMembersWithUsername(Long groupId);
}