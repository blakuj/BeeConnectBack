package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.NotificationDTO;
import com.beconnect.beeconnect_backend.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/notifications
     * Pobierz wszystkie powiadomienia użytkownika
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getMyNotifications();
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * GET /api/notifications/unread
     * Pobierz nieprzeczytane powiadomienia
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getUnreadNotifications();
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    /**
     * GET /api/notifications/unread/count
     * Pobierz liczbę nieprzeczytanych powiadomień
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        try {
            long count = notificationService.getUnreadCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
    }

    /**
     * PUT /api/notifications/{id}/read
     * Oznacz powiadomienie jako przeczytane
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/notifications/read-all
     * Oznacz wszystkie powiadomienia jako przeczytane
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/notifications/{id}
     * Usuń powiadomienie
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/notifications/cleanup
     * Usuń stare przeczytane powiadomienia
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupOldNotifications() {
        try {
            notificationService.cleanupOldNotifications();
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}