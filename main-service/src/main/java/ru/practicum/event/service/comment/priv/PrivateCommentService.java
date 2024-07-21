package ru.practicum.event.service.comment.priv;

import ru.practicum.event.dto.CommentDto;

import java.util.List;

public interface PrivateCommentService {
    List<CommentDto> getByEventIdAndUserId(Long userId, Long eventId);

    CommentDto save(Long userId, Long eventId, CommentDto comment);

    CommentDto update(Long userId, Long eventId, Long commentId, CommentDto comment);

    void delete(Long userId, Long eventId, Long commentId);
}