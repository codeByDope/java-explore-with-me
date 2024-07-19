package ru.practicum.event.service.publ;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.ResponseDto;
import ru.practicum.event.EventMapper;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.SortState;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository repository;
    private final CategoryRepository categoryRepository;
    private final EventMapper mapper = EventMapper.INSTANCE;
    private final StatsClient statsClient;
    @Value("${server.application.name:ewm-service}")
    private String applicationName;

    @Override
    public List<EventShortDto> get(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortState sort, Integer from, Integer size, HttpServletRequest request) {
        int page = (from / size);
        Pageable pageable = PageRequest.of(page, size);

        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Incorrect dateTime");
        }

        if (categories == null) {
            categories = categoryRepository.findAllId();
        }

        if (rangeEnd == null && rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        List<Event> events = repository.search(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable)
                .getContent();

        Map<Long, Long> views = getViews(events);

        List<EventShortDto> result = mapper.modelListToEventShortDto(events);
        result = result.stream()
                .peek(event -> event.setViews(views.getOrDefault(event.getId(), 0L)))
                .peek(event -> event.setConfirmedRequests(getConfirmedRequests(event.getId())))
                .collect(Collectors.toList());

        sendHit(request);

        if (sort != null && sort == SortState.EVENT_DATE) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        } else if (sort != null && sort == SortState.VIEWS) {
            result = result.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }

        return result;
    }


    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }

        Map<Long, Long> views = getViews(List.of(event));

        EventFullDto result = mapper.toEventFullDto(event);
        result.setConfirmedRequests(getConfirmedRequests(result.getId()));
        result.setViews(views.getOrDefault(event.getId(), 0L));

        sendHit(request);

        return result;
    }

    private Map<Long, Long> getViews(List<Event> events) {
        LocalDateTime start = events.stream()
                .map(Event::getCreated)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException(""));

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<ResponseDto> views = statsClient.getStats(start, LocalDateTime.now(), uris, true);

        Map<Long, Long> eventViews = new HashMap<>();

        for (ResponseDto view : views) {
            if (view.getUri().equals("/events")) {
                continue;
            }
            Long eventId = Long.parseLong(view.getUri().substring("/events".length() + 1));
            eventViews.put(eventId, view.getHits());
        }

        return eventViews;
    }

    private void sendHit(HttpServletRequest request) {
        statsClient.hit(RequestDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private Integer getConfirmedRequests(Long eventId) {
        return repository.countConfirmedRequestsByEventId(eventId);
    }
}
