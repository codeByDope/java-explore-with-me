package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.SortState;
import ru.practicum.event.service.publ.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
@Validated
public class PublicEventController {
    private final PublicEventService service;
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public ResponseEntity<List<EventShortDto>> get(@RequestParam(required = false) String text,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) Boolean paid,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
                                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                   @RequestParam(required = false) SortState sort,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   HttpServletRequest request) {
        log.info("Public запрос событий с параметрами поиска");
        List<EventShortDto> result = service.get(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getById(@PathVariable Long id,
                                                HttpServletRequest request) {
        log.info("Public запрос события с ID {}", id);
        EventFullDto result = service.getById(id, request);

        return ResponseEntity.ok(result);
    }
}
