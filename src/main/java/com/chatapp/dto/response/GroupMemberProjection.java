package com.chatapp.dto.response;

import java.time.LocalDateTime;

public interface GroupMemberProjection {

    Long getUserId();

    String getUsername();

    Boolean getAdmin();

    LocalDateTime getJoinedAt();

    String getProfileImage();
}
