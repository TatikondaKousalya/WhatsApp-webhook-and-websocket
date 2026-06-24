package com.chatapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypingMessage {

    private Long roomId;

    private Long senderId;

    private String senderName;

}

