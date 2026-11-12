package com.lectoria_api.notifications.infrastructure.input.rest.rest.controller;

import com.lectoria_api.notifications.domain.ports.input.NotifierUseCases;
import com.lectoria_api.notifications.infrastructure.input.rest.dto.NotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.rest.mapper.NotificationInputMapper;
import com.lectoria_api.notifications.infrastructure.output.persistence.entities.SentNotificationEntity;
import com.lectoria_api.notifications.infrastructure.output.persistence.repository.JpaSentNotificationRepository;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotifierUseCases notificationService;
    private final NotificationInputMapper mapper;
    private final JpaSentNotificationRepository sentNotifications;
    private final SharedAuthUseCases auth;

    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequestDTO notificationDTO) {
        notificationService.executeSendNotification(mapper.toModel(notificationDTO));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/in-app")
    public Page<SentNotificationEntity> inAppNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        if (page < 0 || pageSize < 1 || pageSize > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La paginación no es válida");
        }
        return sentNotifications.findAllByUserIdOrderByCreatedAtDesc(
                auth.getAuthUserId().toString(), PageRequest.of(page, pageSize));
    }

    @GetMapping("/in-app/unread-count")
    public int unreadCount() {
        return sentNotifications.countByShownEqualsAndUserIdEquals(false, auth.getAuthUserId().toString());
    }

    @PatchMapping("/in-app/{notificationId}/read")
    public ResponseEntity<Void> markRead(@PathVariable String notificationId) {
        updateReadState(notificationId, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/in-app/{notificationId}/unread")
    public ResponseEntity<Void> markUnread(@PathVariable String notificationId) {
        updateReadState(notificationId, false);
        return ResponseEntity.noContent().build();
    }

    private void updateReadState(String notificationId, boolean read) {
        SentNotificationEntity notification = sentNotifications
                .findByNotificationIdAndUserId(notificationId, auth.getAuthUserId().toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la notificación"));
        notification.setShown(read);
        notification.setShownAt(read ? java.time.LocalDateTime.now() : null);
        sentNotifications.save(notification);
    }

}
