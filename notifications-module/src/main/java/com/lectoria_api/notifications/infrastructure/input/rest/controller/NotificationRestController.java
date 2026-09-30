package com.lectoria_api.notifications.infrastructure.input.rest.controller;

import com.lectoria_api.notifications.domain.ports.input.NotifierUseCases;
import com.lectoria_api.notifications.infrastructure.input.dto.NotificationRequestDTO;
import com.lectoria_api.notifications.infrastructure.input.mapper.NotificationInputMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotifierUseCases notificationService;
    private final NotificationInputMapper mapper;

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequestDTO notificationDTO) {
        notificationService.executeSendNotification(mapper.toModel(notificationDTO));
        return ResponseEntity.ok().build();
    }

}
