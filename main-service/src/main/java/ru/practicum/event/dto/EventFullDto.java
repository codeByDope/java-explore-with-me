package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private Long id;

    private String annotation;

    private String description;

    private CategoryDto category;

    private Long confirmedRequests;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime createdOn;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration = true;

    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime publishedOn;

    private EventState state;

    private String title;

    private Long views;
}
