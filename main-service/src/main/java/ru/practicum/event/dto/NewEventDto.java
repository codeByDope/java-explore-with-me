package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private Long category;

    @NotNull
    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration = true;

    private Boolean paid;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
