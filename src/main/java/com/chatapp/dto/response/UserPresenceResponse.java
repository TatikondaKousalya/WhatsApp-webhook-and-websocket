package com.chatapp.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPresenceResponse {

    private Long userId;
    private String status;
    private LocalDateTime lastSeen;
    private String socketSession;
}
