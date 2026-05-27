package com.sarvamchat.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class ChatRequest {

    private UUID conversationId;

    @NotBlank
    private String message;

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
