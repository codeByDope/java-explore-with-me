package ru.practicum.event.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.EventMapper;
import ru.practicum.event.dto.AdminStateAction;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper = EventMapper.INSTANCE;
    private final LocationMapper locationMapper = LocationMapper.INSTANCE;

    @Override
    public List<EventFullDto> get(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size) {
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<Event> events;

        if (users == null) {
            users = userRepository.findAllId();
        }
        if (categories == null) {
            categories = categoryRepository.findAllId();
        }
        if (states == null) {
            states = List.of(EventState.PUBLISHED, EventState.CANCELED, EventState.PENDING);
        }

        if (rangeStart != null && rangeEnd != null) {
            events = repository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(users, states, categories, rangeStart, rangeEnd, pageable)
                    .getContent();
        } else if (rangeStart != null) {
            events = repository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsAfter(users, states, categories, rangeStart, pageable)
                    .getContent();
        } else if (rangeEnd != null) {
            events = repository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateIsBefore(users, states, categories, rangeEnd, pageable)
                    .getContent();
        } else {
            events = repository.findAllByInitiatorIdInAndStateInAndCategoryIdIn(users, states, categories, pageable).getContent();
        }

        return mapper.modelListToFullDto(events);
    }

    @Override
    public EventFullDto update(long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        LocalDateTime newDate = updateEvent.getEventDate();
        if (newDate != null) {
            if (newDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Field: eventDate. " +
                        "Error: должно содержать дату, которая еще не наступила. " +
                        "Value: " + newDate);
            } else {
                event.setEventDate(newDate);
            }
        }

        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getCategory() != null) {
            Category category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", updateEvent.getCategory())));
            event.setCategory(category);
        }

        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toModel(updateEvent.getLocation()));
            event.setLocation(location);
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }

        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            event.setTitle(updateEvent.getTitle());
        }

        AdminStateAction stateAction = updateEvent.getStateAction();
        if (stateAction != null) {
            if (stateAction == AdminStateAction.PUBLISH_EVENT) {
                if (event.getState() == EventState.PENDING) {
                    event.setState(EventState.PUBLISHED);
                    event.setPublished(LocalDateTime.now());
                } else {
                    throw new DataIntegrityViolationException("Event must have state PENDING");
                }
            } else {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new DataIntegrityViolationException("Event must have state PENDING or CANCELED");
                } else {
                    event.setState(EventState.CANCELED);
                }
            }
        }

        EventFullDto result = mapper.toEventFullDto(repository.save(event));

        return result;
    }
}
