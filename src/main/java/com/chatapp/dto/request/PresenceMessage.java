package com.chatapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresenceMessage {

    private Long userId;

    private String username;

    private boolean online;

}


