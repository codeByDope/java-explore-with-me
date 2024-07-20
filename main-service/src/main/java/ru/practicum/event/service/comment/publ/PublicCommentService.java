package ru.practicum.event.service.comment.publ;

import ru.practicum.event.dto.CommentDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentDto> getByEventId(Long eventId, Integer from, Integer size);
}