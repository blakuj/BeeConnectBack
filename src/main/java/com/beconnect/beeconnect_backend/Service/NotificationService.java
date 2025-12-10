package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.NotificationDTO;
import com.beconnect.beeconnect_backend.Enum.NotificationType;
import com.beconnect.beeconnect_backend.Model.Notification;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.NotificationRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    /**
     * Pobierz wszystkie powiadomienia zalogowanego użytkownika
     */
    public List<NotificationDTO> getMyNotifications() {
        Person currentUser = personService.getProfile();
        List<Notification> notifications = notificationRepository.findByUserOrderByIsReadAscCreatedAtDesc(currentUser);

        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz nieprzeczytane powiadomienia
     */
    public List<NotificationDTO> getUnreadNotifications() {
        Person currentUser = personService.getProfile();
        List<Notification> notifications = notificationRepository.findUnreadByUser(currentUser);

        return notifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Policz nieprzeczytane powiadomienia
     */
    public long getUnreadCount() {
        Person currentUser = personService.getProfile();
        return notificationRepository.countUnreadByUser(currentUser);
    }

    /**
     * Oznacz powiadomienie jako przeczytane
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Person currentUser = personService.getProfile();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Sprawdź czy powiadomienie należy do użytkownika
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Oznacz wszystkie powiadomienia jako przeczytane
     */
    @Transactional
    public void markAllAsRead() {
        Person currentUser = personService.getProfile();
        notificationRepository.markAllAsReadByUser(currentUser);
    }

    /**
     * Utwórz nowe powiadomienie
     */
    @Transactional
    public NotificationDTO createNotification(Long userId, NotificationType type, String title,
                                              String message, String actionUrl, Long relatedEntityId) {
        Person user = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .actionUrl(actionUrl)
                .relatedEntityId(relatedEntityId)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        return mapToDTO(notification);
    }

    /**
     * Usuń powiadomienie
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        Person currentUser = personService.getProfile();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // Sprawdź czy powiadomienie należy do użytkownika
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        notificationRepository.delete(notification);
    }

    /**
     * Usuń stare przeczytane powiadomienia (starsze niż 30 dni)
     */
    @Transactional
    public void cleanupOldNotifications() {
        Person currentUser = personService.getProfile();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteOldReadNotifications(currentUser, thirtyDaysAgo);
    }


    /**
     * Powiadomienie o zatwierdzeniu pszczelarza
     */
    public void notifyBeekeeperVerified(Long userId) {
        createNotification(
                userId,
                NotificationType.BEEKEEPER_VERIFIED,
                "Weryfikacja zakończona sukcesem",
                "Twój wniosek o weryfikację pszczelarza został zatwierdzony!",
                "profile.html",
                null
        );
    }

    /**
     * Powiadomienie o odrzuceniu pszczelarza
     */
    public void notifyBeekeeperRejected(Long userId, String reason) {
        createNotification(
                userId,
                NotificationType.BEEKEEPER_REJECTED,
                "Weryfikacja odrzucona",
                "Twój wniosek o weryfikację pszczelarza został odrzucony. Powód: " + reason,
                "profile.html",
                null
        );
    }

    /**
     * Powiadomienie o rezerwacji obszaru
     */
    public void notifyAreaReserved(Long ownerId, String areaName, String renterName, Long reservationId) {
        createNotification(
                ownerId,
                NotificationType.AREA_RESERVED,
                "Nowa rezerwacja",
                renterName + " zarezerwował Twój obszar: " + areaName,
                "profile.html",
                reservationId
        );
    }

    /**
     * Powiadomienie o potwierdzeniu rezerwacji
     */
    public void notifyReservationConfirmed(Long renterId, String areaName, Long reservationId) {
        createNotification(
                renterId,
                NotificationType.RESERVATION_CONFIRMED,
                "Rezerwacja potwierdzona",
                "Twoja rezerwacja obszaru \"" + areaName + "\" została potwierdzona",
                "profile.html",
                reservationId
        );
    }

    /**
     * Powiadomienie o nowej wiadomości
     */
    public void notifyNewMessage(Long recipientId, String senderName, Long conversationId) {
        createNotification(
                recipientId,
                NotificationType.NEW_MESSAGE,
                "Nowa wiadomość",
                "Otrzymałeś nową wiadomość od: " + senderName,
                "chat.html?conversation=" + conversationId,
                conversationId
        );
    }

    /**
     * Powiadomienie o nowym zamówieniu
     */
    public void notifyNewOrder(Long sellerId, String productName, String buyerName, Long orderId) {
        createNotification(
                sellerId,
                NotificationType.NEW_ORDER,
                "Nowe zamówienie",
                buyerName + " kupił Twój produkt: " + productName,
                "profile.html",
                orderId
        );
    }

    /**
     * Mapowanie Notification → NotificationDTO
     */
    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .actionUrl(notification.getActionUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .relatedEntityId(notification.getRelatedEntityId())
                .build();
    }
}