package ru.practicum.event.service.comment.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.event.CommentMapper;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.CommentState;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentRepository repository;
    private final CommentMapper mapper = CommentMapper.INSTANCE;

    @Override
    public List<CommentDto> get(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<Comment> comments = repository.findAll(pageable).getContent();

        return mapper.toDtoList(comments);
    }

    @Override
    public CommentDto updateStatus(Long commentId, Boolean accepted) {
        Comment commentForUpdate = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (accepted && commentForUpdate.getState() == CommentState.PENDING) {
            commentForUpdate.setState(CommentState.PUBLISHED);
        } else if (!accepted && commentForUpdate.getState() == CommentState.PENDING) {
            commentForUpdate.setState(CommentState.REJECTED);
        } else {
            throw new DataIntegrityViolationException("It is necessary to have state pending to change it");
        }

        CommentDto result = mapper.toDto(repository.save(commentForUpdate));

        return result;
    }

    @Override
    public void delete(Long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found", commentId)));

        repository.deleteById(commentId);
    }
}