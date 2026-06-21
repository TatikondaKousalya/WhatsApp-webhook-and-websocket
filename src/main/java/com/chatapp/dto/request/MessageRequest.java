package com.chatapp.dto.request;

import com.chatapp.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    // Receiver user ID (for private chat)
    private Long receiverId;

    // Group ID (for group chat)
    private Long groupId;

    @NotBlank(message = "Message cannot be empty")
    private String message;

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    // Attachment file name or URL (optional)
    private String attachmentUrl;
}