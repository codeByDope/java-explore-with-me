package ru.practicum.event.service.admin;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> get(List<Long> users, List<EventState> states, List<Long> categories,
                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    EventFullDto update(long eventId, UpdateEventAdminRequest updateEvent);
}
