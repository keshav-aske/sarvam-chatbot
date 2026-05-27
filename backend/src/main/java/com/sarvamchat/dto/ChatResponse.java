package com.sarvamchat.dto;

import java.util.UUID;

public class ChatResponse {
    private UUID conversationId;
    private String reply;

    public ChatResponse() {}

    public ChatResponse(UUID conversationId, String reply) {
        this.conversationId = conversationId;
        this.reply = reply;
    }

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
}
