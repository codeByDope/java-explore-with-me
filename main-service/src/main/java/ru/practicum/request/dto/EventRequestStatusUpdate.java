package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.model.RequestStatus;

import java.util.List;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdate {
    private List<Long> requestIds;

    private RequestStatus status;
}
