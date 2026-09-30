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
    private String notificationId;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String notificationContent;
    @Column(nullable = false)
    private boolean shown;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime shownAt;

}
