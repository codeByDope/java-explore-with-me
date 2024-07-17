package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "category.id", source = "newEventDto.category")
    Event toEvent(NewEventDto newEventDto);

    @Mapping(target = "confirmedRequests", source = "event.confirmedRequests")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "confirmedRequests", source = "event.confirmedRequests")
    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> modelListToEventShortDto(List<Event> events);
}
