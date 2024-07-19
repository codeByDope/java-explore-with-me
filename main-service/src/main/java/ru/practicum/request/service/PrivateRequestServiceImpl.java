package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository repository;
    private final RequestMapper mapper = RequestMapper.INSTANCE;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        List<Request> requests = repository.findAllByRequesterId(userId);

        return mapper.modelListToDto(requests);
    }

    @Override
    public ParticipationRequestDto save(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%s was not found", eventId)));
        Long confirmedRequests = (long) eventRepository.countConfirmedRequestsByEventId(eventId);
        if (userId == event.getInitiator().getId()) {
            throw new DataIntegrityViolationException("Initiator can't add a request to his event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException("Event must have status published");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(confirmedRequests)) {
            throw new DataIntegrityViolationException("The participant limit has been reached");
        }

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        try {
            Request result = repository.save(request);
            return mapper.modelToDto(result);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        Request request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%s was not found", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        return mapper.modelToDto(repository.save(request));
    }
}
