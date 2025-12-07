package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.*;
import com.beconnect.beeconnect_backend.Model.Conversation;
import com.beconnect.beeconnect_backend.Model.Message;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Repository.ConversationRepository;
import com.beconnect.beeconnect_backend.Repository.MessageRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Repository.ProductRepository;
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
    private ProductRepository productRepository; // Potrzebne do pobrania produktu

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Pobierz wszystkie konwersacje zalogowanego użytkownika
     */
    public List<ConversationDTO> getMyConversations() {
        Person currentUser = personService.getProfile();

        // ZMIANA: Szukamy po nowym query (jako kupujący lub sprzedawca produktu)
        List<Conversation> conversations = conversationRepository.findAllByParticipant(currentUser);

        return conversations.stream()
                .map(conv -> mapConversationToDTO(conv, currentUser))
                .sorted((c1, c2) -> c2.getLastMessageAt().compareTo(c1.getLastMessageAt())) // Sortuj od najnowszych
                .collect(Collectors.toList());
    }

    /**
     * Pobierz wiadomości z konkretnej konwersacji
     */
    public List<MessageDTO> getMessages(Long conversationId) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // ZMIANA: Weryfikacja uczestnictwa (metoda pomocnicza poniżej)
        if (!isUserParticipant(conversation, currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        List<Message> messages = messageRepository.findByConversationOrderBySentAtAsc(conversation);

        return messages.stream()
                .map(msg -> mapMessageToDTO(msg, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Wyślij wiadomość w istniejącej konwersacji
     */
    @Transactional
    public MessageDTO sendMessage(CreateMessageDTO dto) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!isUserParticipant(conversation, currentUser)) {
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

        // ZMIANA: Pobieranie drugiego uczestnika nową metodą
        Person otherUser = getOtherParticipant(conversation, currentUser);

        notificationService.notifyNewMessage(
                otherUser.getId(),
                currentUser.getFirstname() + " " + currentUser.getLastname(),
                conversation.getId()
        );

        return mapMessageToDTO(message, currentUser);
    }

    /**
     * Rozpocznij nową konwersację (O PRODUKCIE)
     */
    @Transactional
    public ConversationDTO startConversation(StartConversationDTO dto) {
        Person currentUser = personService.getProfile();

        // ZMIANA: Pobieramy Produkt, a nie usera bezpośrednio
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Person seller = product.getSeller();

        // Sprawdź czy użytkownik nie pisze sam do siebie (jest sprzedawcą)
        if (seller.getId().equals(currentUser.getId())) {
            throw new RuntimeException("You cannot start a conversation about your own product");
        }


        Conversation conversation = conversationRepository.findByBuyerAndProduct(currentUser, product)
                .orElse(null);

        if (conversation == null) {
            conversation = Conversation.builder()
                    .buyer(currentUser)
                    .product(product)
                    .startedAt(LocalDateTime.now())
                    .build();
            conversation = conversationRepository.save(conversation);
        }


        if (dto.getInitialMessage() != null && !dto.getInitialMessage().trim().isEmpty()) {
            Message message = Message.builder()
                    .conversation(conversation)
                    .sender(currentUser)
                    .content(dto.getInitialMessage().trim())
                    .isRead(false)
                    .build();
            messageRepository.save(message);


        }

        return mapConversationToDTO(conversation, currentUser);
    }

    @Transactional
    public void markAsRead(Long conversationId) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!isUserParticipant(conversation, currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        messageRepository.markAllAsRead(conversation, currentUser);
    }

    public ConversationDTO getConversation(Long conversationId) {
        Person currentUser = personService.getProfile();

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!isUserParticipant(conversation, currentUser)) {
            throw new RuntimeException("You don't have access to this conversation");
        }

        return mapConversationToDTO(conversation, currentUser);
    }


    /**
     * Sprawdza, czy user jest kupującym LUB sprzedawcą produktu w konwersacji
     */
    private boolean isUserParticipant(Conversation conversation, Person user) {
        Long userId = user.getId();
        Long buyerId = conversation.getBuyer().getId();
        Long sellerId = conversation.getProduct().getSeller().getId(); // Seller z produktu

        return userId.equals(buyerId) || userId.equals(sellerId);
    }

    /**
     * Zwraca "tą drugą osobę" w konwersacji
     */
    private Person getOtherParticipant(Conversation conversation, Person currentUser) {
        Person buyer = conversation.getBuyer();
        Person seller = conversation.getProduct().getSeller();

        if (currentUser.getId().equals(buyer.getId())) {
            return seller;
        } else {
            return buyer;
        }
    }

    /**
     * Mapowanie Conversation → ConversationDTO
     */
    private ConversationDTO mapConversationToDTO(Conversation conversation, Person currentUser) {
        Person otherUser = getOtherParticipant(conversation, currentUser);
        long unreadCount = messageRepository.countUnreadMessages(conversation, currentUser);

        String lastMsgContent = "";
        LocalDateTime lastMsgTime = conversation.getStartedAt();


        return ConversationDTO.builder()
                .id(conversation.getId())
                .otherUserId(otherUser.getId())
                .otherUserFirstname(otherUser.getFirstname())
                .otherUserLastname(otherUser.getLastname())
                .otherUserEmail(otherUser.getEmail())
                .productId(conversation.getProduct().getId())
                .productName(conversation.getProduct().getName())
                .productImage(conversation.getProduct().getImages().isEmpty() ? null : conversation.getProduct().getImages().get(0).getFileContent())

                .lastMessageContent(lastMsgContent)
                .lastMessageAt(lastMsgTime)
                .unreadCount((int) unreadCount)
                .createdAt(conversation.getStartedAt()) // lub getCreatedAt
                .build();
    }

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