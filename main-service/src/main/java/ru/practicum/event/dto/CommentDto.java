package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.CommentState;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;

    @NotNull
    @NotBlank
    private String text;

    private EventShortDto event;

    private UserShortDto author;

    private LocalDateTime created;

    private CommentState state;
}
