package com.sarvamchat.service;

import com.sarvamchat.dto.ChatResponse;
import com.sarvamchat.model.Conversation;
import com.sarvamchat.model.Message;
import com.sarvamchat.repository.ConversationRepository;
import com.sarvamchat.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT =
            "You are a helpful, concise general-purpose assistant. Answer clearly and accurately.";

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SarvamService sarvamService;

    public ChatService(ConversationRepository conversationRepository,
                       MessageRepository messageRepository,
                       SarvamService sarvamService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.sarvamService = sarvamService;
    }

    @Transactional
    public ChatResponse chat(UUID conversationId, String userMessage) {
        Conversation conversation;
        List<Message> history;

        if (conversationId == null) {
            Conversation c = new Conversation();
            c.setTitle(makeTitle(userMessage));
            conversation = conversationRepository.save(c);
            history = List.of();
        } else {
            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));
            history = new ArrayList<>(conversation.getMessages());
        }

        Message userMsg = new Message();
        userMsg.setConversation(conversation);
        userMsg.setRole(Message.Role.user);
        userMsg.setContent(userMessage);
        messageRepository.save(userMsg);

        List<Map<String, String>> apiMessages = new ArrayList<>();
        apiMessages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        for (Message m : history) {
            apiMessages.add(Map.of("role", m.getRole().name(), "content", m.getContent()));
        }
        apiMessages.add(Map.of("role", "user", "content", userMessage));

        String reply = sarvamService.complete(apiMessages);

        Message assistantMsg = new Message();
        assistantMsg.setConversation(conversation);
        assistantMsg.setRole(Message.Role.assistant);
        assistantMsg.setContent(reply);
        messageRepository.save(assistantMsg);

        conversation.touch();
        conversationRepository.save(conversation);

        return new ChatResponse(conversation.getId(), reply);
    }

    private String makeTitle(String firstMessage) {
        return firstMessage.length() > 60 ? firstMessage.substring(0, 60) + "..." : firstMessage;
    }
}
