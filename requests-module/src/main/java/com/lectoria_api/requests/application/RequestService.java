package com.lectoria_api.requests.application;

import com.lectoria_api.chapters.domain.ports.output.ChapterRepositoryPort;
import com.lectoria_api.notifications.domain.model.NotificationModel;
import com.lectoria_api.notifications.domain.model.union.InAppNotificationModel;
import com.lectoria_api.notifications.domain.ports.input.NotifierUseCases;
import com.lectoria_api.requests.domain.RequestStatus;
import com.lectoria_api.requests.domain.RequestType;
import com.lectoria_api.requests.infrastructure.persistence.*;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.users.domain.model.UserModel;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;
import com.lectoria_api.users.domain.ports.input.SharedUserUseCases;
import com.lectoria_api.verifications.domain.model.enums.VerificationType;
import com.lectoria_api.domain.ports.input.VerificationUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.lectoria_api.notifications.domain.constants.NotificationTypes.IN_APP_SERVICE;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requests;
    private final CoauthorRepository coauthors;
    private final ChapterLockRepository locks;
    private final SharedAuthUseCases auth;
    private final SharedUserUseCases users;
    private final StoryUseCases stories;
    private final ChapterRepositoryPort chapterRepository;
    private final NotifierUseCases notifier;
    private final VerificationUseCases verifications;

    @Transactional
    public RequestEntity requestCoauthor(UUID storyId) {
        UUID userId = auth.getAuthUserId();
        StoryModel story = stories.story(storyId);
        UUID ownerId = story.getUserId();
        if (ownerId.equals(userId)) throw conflict("Ya eres el autor de esta historia");
        return createCoauthorRequest(storyId, userId, ownerId, userId);
    }

    @Transactional
    public RequestEntity inviteCoauthor(UUID storyId, String username) {
        stories.ensureOwnedStory(storyId);
        UserModel invitee = users.findAnyUserByUsername(username, true);
        if (invitee.getUserRoles().stream().anyMatch(role -> "LECTORIA_ADMIN".equals(role.getRoleName()))) {
            throw conflict("No se puede invitar a un usuario administrador como coautor");
        }
        if (invitee.getUserId().equals(auth.getAuthUserId())) throw conflict("No puedes invitarte a ti mismo");
        return createCoauthorRequest(storyId, auth.getAuthUserId(), invitee.getUserId(), invitee.getUserId());
    }

    private RequestEntity createCoauthorRequest(UUID storyId, UUID source, UUID target, UUID collaboratorId) {
        if (coauthors.existsByStoryIdAndUserId(storyId, collaboratorId)) throw conflict("Este usuario ya es coautor de la historia");
        if (requests.existsByTypeAndStatusAndSourceUserIdAndTargetUserIdAndStoryId(
                RequestType.COAUTHOR_REQUEST, RequestStatus.PENDING, source, target, storyId)) {
            throw conflict("Ya existe una solicitud pendiente para esta historia");
        }
        RequestEntity request = new RequestEntity();
        request.setType(RequestType.COAUTHOR_REQUEST);
        request.setStatus(RequestStatus.PENDING);
        request.setSourceUserId(source);
        request.setTargetUserId(target);
        request.setStoryId(storyId);
        request.setCoauthorUserId(collaboratorId);
        request.setCreatedAt(LocalDateTime.now());
        RequestEntity saved = requests.save(request);
        notifyUser(target, "Tienes una nueva solicitud de coautoría para una historia.");
        return saved;
    }

    public List<RequestEntity> myRequests() {
        UUID userId = auth.getAuthUserId();
        return requests.findAllBySourceUserIdOrTargetUserIdOrderByCreatedAtDesc(userId, userId);
    }

    @Transactional
    public RequestEntity respond(UUID requestId, boolean accept) {
        UUID userId = auth.getAuthUserId();
        RequestEntity request = requests.findById(requestId).orElseThrow(() -> notFound("No se encontró la solicitud"));
        if (request.getType() != RequestType.COAUTHOR_REQUEST || !userId.equals(request.getTargetUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes responder esta solicitud");
        }
        if (request.getStatus() != RequestStatus.PENDING) throw conflict("La solicitud ya fue respondida");
        if (accept && coauthors.existsByStoryIdAndUserId(request.getStoryId(), request.getCoauthorUserId())) {
            throw conflict("Este usuario ya es coautor de la historia");
        }
        request.setStatus(accept ? RequestStatus.ACCEPTED : RequestStatus.REJECTED);
        request.setResolvedAt(LocalDateTime.now());
        if (accept) {
            CoauthorEntity coauthor = new CoauthorEntity();
            coauthor.setStoryId(request.getStoryId());
            coauthor.setUserId(request.getCoauthorUserId());
            coauthor.setCreatedAt(LocalDateTime.now());
            coauthors.save(coauthor);
        }
        RequestEntity saved = requests.save(request);
        notifyUser(request.getSourceUserId(), accept
                ? "Tu solicitud de coautoría fue aceptada."
                : "Tu solicitud de coautoría fue rechazada.");
        return saved;
    }

    public List<CoauthorEntity> storyCoauthors(UUID storyId) {
        stories.ensureOwnedStory(storyId);
        return coauthors.findAllByStoryIdOrderByCreatedAtAsc(storyId);
    }

    public List<StoryModel> myCoauthoredStories() {
        return coauthors.findAllByUserIdOrderByCreatedAtDesc(auth.getAuthUserId()).stream()
                .map(coauthor -> stories.story(coauthor.getStoryId()))
                .toList();
    }

    @Transactional
    public void removeCoauthor(UUID storyId, UUID userId) {
        stories.ensureOwnedStory(storyId);
        List<UUID> chapterIds = chapterRepository.chaptersByStory(storyId).stream()
                .map(chapter -> chapter.getId())
                .toList();
        if (!chapterIds.isEmpty()) locks.deleteAllByUserIdAndChapterIdIn(userId, chapterIds);
        coauthors.deleteByStoryIdAndUserId(storyId, userId);
    }

    @Transactional
    public RequestEntity requestReactivation(String username, String email) {
        boolean hasUsername = username != null && !username.isBlank();
        boolean hasEmail = email != null && !email.isBlank();
        if (hasUsername == hasEmail) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Indica un nombre de usuario o un correo, pero no ambos");
        UserModel disabledUser = hasUsername
                ? users.findAnyUserByUsername(username.trim(), false)
                : users.findAnyUserByUserEmail(email.trim(), false);
        if (disabledUser.getUserDetails() == null || !disabledUser.getUserDetails().isRegisteredVerified()) {
            throw conflict("Esta cuenta no puede solicitar reactivación");
        }
        if (requests.existsByTypeAndStatusAndSourceUserIdAndTargetUserIdAndStoryId(
                RequestType.REACTIVATE_ACCOUNT, RequestStatus.PENDING,
                disabledUser.getUserId(), null, null)) {
            throw conflict("Ya existe una solicitud de reactivación pendiente para esta cuenta");
        }
        RequestEntity request = new RequestEntity();
        request.setType(RequestType.REACTIVATE_ACCOUNT);
        request.setStatus(RequestStatus.PENDING);
        request.setSourceUserId(disabledUser.getUserId());
        request.setCreatedAt(LocalDateTime.now());
        RequestEntity saved = requests.save(request);
        users.findEnabledUsersByRoleName("LECTORIA_ADMIN").forEach(admin ->
                notifyUser(admin.getUserId(), "Un usuario solicitó ayuda para reactivar su cuenta."));
        return saved;
    }

    public List<RequestEntity> pendingReactivations() {
        return requests.findAllByTypeAndStatusOrderByCreatedAtDesc(RequestType.REACTIVATE_ACCOUNT, RequestStatus.PENDING);
    }

    @Transactional
    public void beginReactivation(UUID requestId) {
        RequestEntity request = requests.findById(requestId).orElseThrow(() -> notFound("No se encontró la solicitud"));
        if (request.getType() != RequestType.REACTIVATE_ACCOUNT || request.getStatus() != RequestStatus.PENDING) {
            throw conflict("La solicitud de reactivación ya no está pendiente");
        }
        UserModel user = users.findUserById(request.getSourceUserId());
        if (user.isUserEnabled()) throw conflict("La cuenta ya está activa");
        verifications.createVerification(null, null, VerificationType.REACTIVATE_ACCOUNT,
                user.getUserEmail(), user.getUserId());
        request.setStatus(RequestStatus.ACCEPTED);
        request.setResolvedAt(LocalDateTime.now());
        requests.save(request);
        notifyUser(user.getUserId(), "Un administrador inició la reactivación de tu cuenta. Revisa tu correo para continuar.");
    }

    @Transactional
    public ChapterLockEntity acquireChapterLock(UUID chapterId) {
        UUID userId = auth.getAuthUserId();
        UUID storyId = chapterRepository.findById(chapterId)
                .orElseThrow(() -> notFound("No se encontró el capítulo")).getStoryId();
        if (!stories.isOwnedStory(storyId) && !coauthors.existsByStoryIdAndUserId(storyId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar este capítulo");
        }
        locks.deleteAllByExpiresAtBefore(LocalDateTime.now());
        ChapterLockEntity lock = locks.findById(chapterId).orElse(null);
        if (lock != null && !lock.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este capítulo está siendo editado por otra persona");
        }
        if (lock == null) {
            lock = new ChapterLockEntity();
            lock.setChapterId(chapterId);
        }
        lock.setUserId(userId);
        lock.setExpiresAt(LocalDateTime.now().plusMinutes(2));
        return locks.save(lock);
    }

    @Transactional
    public void releaseChapterLock(UUID chapterId) {
        locks.deleteByChapterIdAndUserId(chapterId, auth.getAuthUserId());
    }

    private void notifyUser(UUID userId, String message) {
        notifier.executeSendNotification(NotificationModel.builder()
                .notificationType(IN_APP_SERVICE)
                .notificationData(InAppNotificationModel.builder()
                        .userId(userId.toString())
                        .notificationContent(message)
                        .build())
                .build());
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
