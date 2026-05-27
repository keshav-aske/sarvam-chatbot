package com.sarvamchat.dto;

import java.time.Instant;
import java.util.UUID;

public class ConversationSummary {
    private UUID id;
    private String title;
    private Instant updatedAt;

    public ConversationSummary(UUID id, String title, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public Instant getUpdatedAt() { return updatedAt; }
}
