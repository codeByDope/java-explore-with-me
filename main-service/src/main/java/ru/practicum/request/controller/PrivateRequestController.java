package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.PrivateRequestService;

import javax.validation.constraints.NotNull;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/requests")
@Validated
public class PrivateRequestController {
    private final PrivateRequestService service;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getByUserId(@PathVariable Long userId) {
        log.info("Запрошены запросы пользователя с ID {}", userId);
        List<ParticipationRequestDto> result = service.getByUserId(userId);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> save(@PathVariable Long userId,
                                                        @RequestParam @NotNull Long eventId) {
        log.info("Добавление запроса пользователя {} на событие {}", userId, eventId);
        ParticipationRequestDto result = service.save(userId, eventId);

        return ResponseEntity.status(201).body(result);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancel(@PathVariable Long userId,
                                                          @PathVariable Long requestId) {
        log.info("Отмена запроса {} пользователем {}", requestId, userId);
        ParticipationRequestDto result = service.cancel(userId, requestId);

        return ResponseEntity.ok(result);
    }
}
