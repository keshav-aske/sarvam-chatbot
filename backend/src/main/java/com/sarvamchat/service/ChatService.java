package com.sarvamchat.service;

import com.sarvamchat.dto.ChatResponse;
import com.sarvamchat.model.Conversation;
import com.sarvamchat.model.Message;
import com.sarvamchat.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT =
            "You are a helpful, concise general-purpose assistant. Answer clearly and accurately.";

    private final ConversationRepository conversationRepository;
    private final SarvamService sarvamService;

    public ChatService(ConversationRepository conversationRepository, SarvamService sarvamService) {
        this.conversationRepository = conversationRepository;
        this.sarvamService = sarvamService;
    }

    @Transactional
    public ChatResponse chat(UUID conversationId, String userMessage) {
        Conversation conversation = (conversationId == null)
                ? createNewConversation(userMessage)
                : conversationRepository.findById(conversationId)
                        .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

        Message userMsg = new Message();
        userMsg.setConversation(conversation);
        userMsg.setRole(Message.Role.user);
        userMsg.setContent(userMessage);
        conversation.getMessages().add(userMsg);

        List<Map<String, String>> apiMessages = buildApiMessages(conversation);
        String reply = sarvamService.complete(apiMessages);

        Message assistantMsg = new Message();
        assistantMsg.setConversation(conversation);
        assistantMsg.setRole(Message.Role.assistant);
        assistantMsg.setContent(reply);
        conversation.getMessages().add(assistantMsg);

        Conversation saved = conversationRepository.save(conversation);
        return new ChatResponse(saved.getId(), reply);
    }

    private Conversation createNewConversation(String firstMessage) {
        Conversation c = new Conversation();
        String title = firstMessage.length() > 60 ? firstMessage.substring(0, 60) + "..." : firstMessage;
        c.setTitle(title);
        return c;
    }

    private List<Map<String, String>> buildApiMessages(Conversation conversation) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        for (Message m : conversation.getMessages()) {
            messages.add(Map.of("role", m.getRole().name(), "content", m.getContent()));
        }
        return messages;
    }
}
