package com.lectoria_api.notifications.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_SENT_NOTIFICATIONS")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SentNotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "NOTIFICATION_ID")
    private String notificationId;
    @Column(name = "USER_ID", nullable = false)
    private String userId;
    @Column(name = "NOTIFICATION_CONTENT", nullable = false)
    private String notificationContent;
    @Column(name = "NOTIFICATION_SHOWN", nullable = false)
    private boolean shown;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE)
    @Column(name = "NOTIFICATION_CREATED_AT")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE)
    @Column(name = "NOTIFICATION_SHOWN_AT")
    private LocalDateTime shownAt;

}
