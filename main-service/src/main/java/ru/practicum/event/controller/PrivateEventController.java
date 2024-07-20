package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.comment.priv.PrivateCommentService;
import ru.practicum.event.service.priv.PrivateEventService;
import ru.practicum.request.dto.EventRequestStatusUpdate;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {
    private final PrivateEventService service;
    private final PrivateCommentService commentService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                      @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Запрошены все события, добавленные пользователем {}", userId);
        List<EventShortDto> result = service.getAll(userId, from, size);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<EventFullDto> save(@PathVariable Long userId,
                                             @RequestBody @Valid NewEventDto newEvent) {
        log.info("Добавление события: {}", newEvent);
        EventFullDto result = service.save(userId, newEvent);

        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getById(@PathVariable Long userId,
                                                @PathVariable Long eventId) {
        log.info("Пользователь {} запросил информацию про свое событие {}", userId, eventId);
        EventFullDto result = service.getById(userId, eventId);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest updateEvent) {
        log.info("Обновление события {} пользователем {} : {}", userId, eventId, updateEvent);
        EventFullDto result = service.update(userId, eventId, updateEvent);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Пользователь {} выгрузил запросы на участие в событии {}", userId, eventId);
        List<ParticipationRequestDto> result = service.getRequests(userId, eventId);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateStatusRequests(@PathVariable Long userId,
                                                                               @PathVariable Long eventId,
                                                                               @RequestBody EventRequestStatusUpdate eventRequestStatusUpdate) {
        log.info("Пользователь {} обновляет статусы запросов для события {} : {}", userId, eventId, eventRequestStatusUpdate);
        EventRequestStatusUpdateResult result = service.updateStatusRequests(userId, eventId, eventRequestStatusUpdate);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentDto>> getUsersCommentsForEvent(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("Запрошены комментарии пользователя {} для события {}");
        List<CommentDto> result = commentService.getByEventIdAndUserId(userId, eventId);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentDto> saveComment(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @RequestBody @Valid CommentDto commentDto) {
        log.info("Пользователь {} оставил комментарий событию {} : {}", userId, eventId, commentDto);
        CommentDto result = commentService.save(userId, eventId, commentDto);

        return ResponseEntity.status(201).body(result);
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @PathVariable Long commentId,
                                                    @RequestBody @Valid CommentDto commentDto) throws AccessDeniedException {
        log.info("Пользователь {} обновляет комментарий {} для события {}", userId, commentId, eventId);
        CommentDto result = commentService.update(userId, eventId, commentId, commentDto);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @PathVariable Long commentId) throws AccessDeniedException {
        log.info("Пользователь {} удаляет комментарий {} для события {}", userId, commentId, eventId);
        commentService.delete(userId, eventId, commentId);

        return ResponseEntity.status(204).build();
    }
}
