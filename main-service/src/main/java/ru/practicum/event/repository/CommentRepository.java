package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdAndAuthorId(Long eventId, Long authorId);

    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);

    Page<Comment> findAll(Pageable pageable);
}