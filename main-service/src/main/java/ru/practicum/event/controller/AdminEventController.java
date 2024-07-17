package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.admin.AdminEventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
@Validated
public class AdminEventController {
    private final AdminEventService service;
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    @GetMapping
    public ResponseEntity<List<EventFullDto>> get(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Long from,
            @RequestParam(defaultValue = "10") Long size) {

        log.info("Запрос события от админа");
        List<EventFullDto> result = service.get(users, states, categories, rangeStart, rangeEnd, from, size);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> update(@PathVariable Long eventId,
                                               @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Админ обновляет событие {}", eventId);
        EventFullDto result = service.update(eventId, updateEventAdminRequest);

        return ResponseEntity.ok(result);
    }
}
