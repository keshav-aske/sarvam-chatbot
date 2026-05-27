package com.sarvamchat.dto;

import java.time.Instant;
import java.util.UUID;

public class MessageDto {
    private UUID id;
    private String role;
    private String content;
    private Instant createdAt;

    public MessageDto(UUID id, String role, String content, Instant createdAt) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public Instant getCreatedAt() { return createdAt; }
}
