package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.service.comment.admin.AdminCommentService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/comments")
@Validated
public class AdminCommentController {
    private final AdminCommentService service;

    @GetMapping
    public ResponseEntity<List<CommentDto>> get(@RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Admin: запрос комментариев");
        List<CommentDto> result = service.get(from, size);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateStatus(@PathVariable Long commentId,
                                                   @RequestParam @NotNull Boolean accepted) {
        log.info("Admin: изменение статуса комментария {}. accepted {}.", commentId, accepted);
        CommentDto result = service.updateStatus(commentId, accepted);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/commentId")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        log.info("Удаление комментария {}", commentId);
        service.delete(commentId);

        return ResponseEntity.status(204).build();
    }
}
