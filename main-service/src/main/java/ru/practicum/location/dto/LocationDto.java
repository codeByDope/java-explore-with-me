package ru.practicum.location.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Float lat;

    private Float lon;
}
