package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.*;
import com.beconnect.beeconnect_backend.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * GET /api/chat/conversations
     * Pobierz wszystkie konwersacje zalogowanego użytkownika
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getMyConversations() {
        try {
            List<ConversationDTO> conversations = chatService.getMyConversations();
            return ResponseEntity.ok(conversations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/chat/conversations/{id}
     * Pobierz szczegóły konwersacji
     */
    @GetMapping("/conversations/{id}")
    public ResponseEntity<?> getConversation(@PathVariable Long id) {
        try {
            ConversationDTO conversation = chatService.getConversation(id);
            return ResponseEntity.ok(conversation);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/chat/conversations/{id}/messages
     * Pobierz wiadomości z konwersacji
     */
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
        try {
            List<MessageDTO> messages = chatService.getMessages(id);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/chat/messages
     * Wyślij wiadomość
     */
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(@RequestBody CreateMessageDTO dto) {
        try {
            MessageDTO message = chatService.sendMessage(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/chat/conversations
     * Rozpocznij nową konwersację
     */
    @PostMapping("/conversations")
    public ResponseEntity<?> startConversation(@RequestBody StartConversationDTO dto) {
        try {
            ConversationDTO conversation = chatService.startConversation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/chat/conversations/{id}/read
     * Oznacz wiadomości jako przeczytane
     */
    @PutMapping("/conversations/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            chatService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}