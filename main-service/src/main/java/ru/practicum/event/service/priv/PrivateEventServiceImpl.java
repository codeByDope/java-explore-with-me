package ru.practicum.event.service.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.EventMapper;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.dto.EventRequestStatusUpdate;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository repository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper mapper = EventMapper.INSTANCE;
    private final LocationMapper locationMapper = LocationMapper.INSTANCE;
    private final RequestMapper requestMapper = RequestMapper.INSTANCE;

    @Override
    public List<EventShortDto> getAll(long userId, Long from, Long size) {
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<Event> events = repository.findAllByInitiatorId(userId, pageable).getContent();

        return mapper.modelListToEventShortDto(events);
    }

    @Override
    public EventFullDto save(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Field: eventDate. " +
                    "Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + newEventDto.getEventDate());
        }

        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }

        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0L);
        }

        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = mapper.toEvent(newEventDto);
        locationRepository.save(event.getLocation());
        event.setCreated(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setState(EventState.PENDING);

        EventFullDto result = mapper.toEventFullDto(repository.save(event));

        return result;
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        return mapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getState() == EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed");
        }

        LocalDateTime updatedEventDate = updateEvent.getEventDate();
        if (updatedEventDate != null) {
            if (updatedEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Field: eventDate. " +
                        "Error: должно содержать дату, которая еще не наступила. " +
                        "Value: " + updatedEventDate);
            } else {
                event.setEventDate(updatedEventDate);
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

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            } else {
                event.setState(EventState.PENDING);
            }
        }

        Event result = repository.save(event);

        return mapper.toEventFullDto(result);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getInitiator().getId() != userId) {
            throw new ValidationException();
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requestMapper.modelListToDto(requests);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequests(long userId, long eventId, EventRequestStatusUpdate requestStatusUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        event.setConfirmedRequests(repository.countConfirmedRequestsByEventId(eventId));

        List<Request> requests = requestRepository.findAllByIdIn(requestStatusUpdate.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            for (Request request : requests) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.modelToDto(requestRepository.save(request)));
            }
        } else {
            Long requestCountToLimit = event.getParticipantLimit() - event.getConfirmedRequests();

            if (requestCountToLimit <= 0) {
                throw new DataIntegrityViolationException("The participant limit has been reached");
            }

            for (Request request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new DataIntegrityViolationException("Request must have status PENDING");
                }

                if (requestStatusUpdate.getStatus().equals(RequestStatus.REJECTED) || requestCountToLimit == 0) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.modelToDto(requestRepository.save(request)));
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(requestMapper.modelToDto(requestRepository.save(request)));
                    requestCountToLimit--;
                }
            }
        }

        repository.save(event);
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
