package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.*;
import com.beconnect.beeconnect_backend.Model.Conversation;
import com.beconnect.beeconnect_backend.Model.Message;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.ConversationRepository;
import com.beconnect.beeconnect_backend.Repository.MessageRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    /**
     * Pobierz wszystkie konwersacje zalogowanego użytkownika
     */
    public List<ConversationDTO> getMyConversations() {
        Person currentUser = personService.getProfile();
        List<Conversation> conversations = conversationRepository.findByUser(currentUser);

        return conversations.stream()
                .map(conv -> mapConversationToDTO(conv, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Pobierz wiadomości z konkretnej konwersacji
     */
    public List<MessageDTO> getMessages(Long conversationId) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Sprawdź czy użytkownik jest uczestnikiem
        if (!conversation.isParticipant(currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        List<Message> messages = messageRepository.findByConversationOrderBySentAtAsc(conversation);

        return messages.stream()
                .map(msg -> mapMessageToDTO(msg, currentUser))
                .collect(Collectors.toList());
    }

    @Autowired
    private NotificationService notificationService;

    /**
     * Wyślij wiadomość w istniejącej konwersacji
     */
    @Transactional
    public MessageDTO sendMessage(CreateMessageDTO dto) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.isParticipant(currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new RuntimeException("Message content cannot be empty");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(dto.getContent().trim())
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        conversation.setLastMessageContent(dto.getContent().trim());
        conversation.setLastMessageAt(message.getSentAt());
        conversationRepository.save(conversation);

        // DODAJ: Wyślij powiadomienie do drugiego uczestnika
        Person otherUser = conversation.getOtherParticipant(currentUser);
        notificationService.notifyNewMessage(
                otherUser.getId(),
                currentUser.getFirstname() + " " + currentUser.getLastname(),
                conversation.getId()
        );

        return mapMessageToDTO(message, currentUser);
    }

    /**
     * Rozpocznij nową konwersację
     */
    @Transactional
    public ConversationDTO startConversation(StartConversationDTO dto) {
        Person currentUser = personService.getProfile();

        // Pobierz drugiego użytkownika
        Person otherUser = personRepository.findById(dto.getOtherUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sprawdź czy użytkownik nie próbuje rozpocząć konwersacji sam ze sobą
        if (currentUser.getId().equals(otherUser.getId())) {
            throw new RuntimeException("Cannot start conversation with yourself");
        }

        // Sprawdź czy konwersacja już istnieje
        Conversation conversation = conversationRepository.findByParticipants(currentUser, otherUser)
                .orElse(null);

        if (conversation == null) {
            // Utwórz nową konwersację
            conversation = Conversation.builder()
                    .participant1(currentUser)
                    .participant2(otherUser)
                    .build();
            conversation = conversationRepository.save(conversation);
        }

        // Jeśli jest początkowa wiadomość, wyślij ją
        if (dto.getInitialMessage() != null && !dto.getInitialMessage().trim().isEmpty()) {
            Message message = Message.builder()
                    .conversation(conversation)
                    .sender(currentUser)
                    .content(dto.getInitialMessage().trim())
                    .isRead(false)
                    .build();
            messageRepository.save(message);

            // Zaktualizuj ostatnią wiadomość
            conversation.setLastMessageContent(dto.getInitialMessage().trim());
            conversation.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(conversation);
        }

        return mapConversationToDTO(conversation, currentUser);
    }

    /**
     * Oznacz wiadomości jako przeczytane
     */
    @Transactional
    public void markAsRead(Long conversationId) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Sprawdź czy użytkownik jest uczestnikiem
        if (!conversation.isParticipant(currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        messageRepository.markAllAsRead(conversation, currentUser);
    }

    /**
     * Pobierz szczegóły konwersacji
     */
    public ConversationDTO getConversation(Long conversationId) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Sprawdź czy użytkownik jest uczestnikiem
        if (!conversation.isParticipant(currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        return mapConversationToDTO(conversation, currentUser);
    }

    /**
     * Mapowanie Conversation → ConversationDTO
     */
    private ConversationDTO mapConversationToDTO(Conversation conversation, Person currentUser) {
        Person otherUser = conversation.getOtherParticipant(currentUser);
        long unreadCount = messageRepository.countUnreadMessages(conversation, currentUser);

        return ConversationDTO.builder()
                .id(conversation.getId())
                .otherUserId(otherUser.getId())
                .otherUserFirstname(otherUser.getFirstname())
                .otherUserLastname(otherUser.getLastname())
                .otherUserEmail(otherUser.getEmail())
                .lastMessageContent(conversation.getLastMessageContent())
                .lastMessageAt(conversation.getLastMessageAt())
                .unreadCount((int) unreadCount)
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    /**
     * Mapowanie Message → MessageDTO
     */
    private MessageDTO mapMessageToDTO(Message message, Person currentUser) {
        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderFirstname(message.getSender().getFirstname())
                .senderLastname(message.getSender().getLastname())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .isMine(message.getSender().getId().equals(currentUser.getId()))
                .build();
    }
}