package com.lectoria_api.notifications.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.lectoria_api.common.domain.constants.Constants.TIME_ZONE;

@Entity
@Table(name = "T_NOTIFICATION_TEMPLATES")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "NOTIFICATION_TEMPLATE_ID")
    private String notificationTemplateId;
    @Column(name = "NOTIFICATION_TEMPLATE_NAME", nullable = false, unique = true)
    private String templateName;
    @Column(name = "NOTIFICATION_TEMPLATE_SUBJECT", nullable = false)
    private String templateSubject;
    @Column(name = "NOTIFICATION_TEMPLATE_LOCATION", nullable = false, unique = true)
    private String templateLocation;

    @Column(name = "NOTIFICATION_TEMPLATE_CREATED_AT", nullable = false, unique = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime createdAt;

    @Column(name = "NOTIFICATION_TEMPLATE_UPDATED_AT", nullable = false, unique = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss", iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now(TIME_ZONE).truncatedTo(ChronoUnit.SECONDS);
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now(TIME_ZONE).truncatedTo(ChronoUnit.SECONDS);
    }

}
