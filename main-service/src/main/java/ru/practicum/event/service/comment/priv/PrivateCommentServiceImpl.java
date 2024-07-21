package ru.practicum.event.service.comment.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.CommentMapper;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.CommentState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NoAccessException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository repository;
    private final CommentMapper mapper = CommentMapper.INSTANCE;

    @Override
    public List<CommentDto> getByEventIdAndUserId(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        List<Comment> comments = repository.findAllByEventIdAndAuthorId(eventId, userId);

        return mapper.toDtoList(comments);
    }

    @Override
    public CommentDto save(Long userId, Long eventId, CommentDto comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Comment commentForSave = mapper.toModel(comment);
        commentForSave.setAuthor(user);
        commentForSave.setEvent(event);
        commentForSave.setState(CommentState.PENDING);
        commentForSave.setCreated(LocalDateTime.now());

        CommentDto result = mapper.toDto(repository.save(commentForSave));
        result.getEvent().setConfirmedRequests(eventRepository.countConfirmedRequestsByEventId(eventId));

        return result;
    }

    @Override
    public CommentDto update(Long userId, Long eventId, Long commentId, CommentDto comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Comment commentForUpdate = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (commentForUpdate.getAuthor().getId().equals(userId)) {
            commentForUpdate.setText(comment.getText());
        } else {
            throw new NoAccessException("Access Denied");
        }

        CommentDto result = mapper.toDto(repository.save(commentForUpdate));

        return result;
    }

    @Override
    public void delete(Long userId, Long eventId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (comment.getAuthor().getId().equals(userId)) {
            repository.deleteById(commentId);
        } else {
            throw new NoAccessException("Access Denied");
        }
    }
}
