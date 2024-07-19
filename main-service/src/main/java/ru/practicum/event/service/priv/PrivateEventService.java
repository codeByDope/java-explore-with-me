package ru.practicum.event.service.priv;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.EventRequestStatusUpdate;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getAll(long userId, Long from, Long size);

    EventFullDto save(long userId, NewEventDto newEventDto);

    EventFullDto getById(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEvent);

    List<ParticipationRequestDto> getRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatusRequests(long userId, long eventId,
                                                        EventRequestStatusUpdate requestStatusUpdate);
}
