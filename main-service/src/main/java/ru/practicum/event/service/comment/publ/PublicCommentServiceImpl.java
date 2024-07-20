package ru.practicum.event.service.comment.publ;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.event.CommentMapper;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.model.CommentState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository repository;
    private final EventRepository eventRepository;
    private final CommentMapper mapper = CommentMapper.INSTANCE;


    @Override
    public List<CommentDto> getByEventId(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<CommentDto> comments = mapper.toDtoList(repository.findAllByEventId(eventId, pageable).getContent());

        comments = comments.stream()
                .filter(comment -> comment.getState() == CommentState.PUBLISHED)
                .collect(Collectors.toList());

        return comments;
    }
}
