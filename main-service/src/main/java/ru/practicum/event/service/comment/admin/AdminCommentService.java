package ru.practicum.event.service.comment.admin;

import ru.practicum.event.dto.CommentDto;

import java.util.List;

public interface AdminCommentService {
    List<CommentDto> get(Integer from, Integer size);

    CommentDto updateStatus(Long commentId, Boolean accepted);

    void delete(Long commentId);
}