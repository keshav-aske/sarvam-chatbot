package com.sarvamchat.controller;

import com.sarvamchat.dto.ChatRequest;
import com.sarvamchat.dto.ChatResponse;
import com.sarvamchat.dto.ConversationSummary;
import com.sarvamchat.dto.MessageDto;
import com.sarvamchat.model.Conversation;
import com.sarvamchat.repository.ConversationRepository;
import com.sarvamchat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;
    private final ConversationRepository conversationRepository;

    public ChatController(ChatService chatService, ConversationRepository conversationRepository) {
        this.chatService = chatService;
        this.conversationRepository = conversationRepository;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request.getConversationId(), request.getMessage());
    }

    @GetMapping("/conversations")
    public List<ConversationSummary> list() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(c -> new ConversationSummary(c.getId(), c.getTitle(), c.getUpdatedAt()))
                .toList();
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<List<MessageDto>> get(@PathVariable UUID id) {
        return conversationRepository.findById(id)
                .map(Conversation::getMessages)
                .map(msgs -> msgs.stream()
                        .map(m -> new MessageDto(m.getId(), m.getRole().name(), m.getContent(), m.getCreatedAt()))
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!conversationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        conversationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
