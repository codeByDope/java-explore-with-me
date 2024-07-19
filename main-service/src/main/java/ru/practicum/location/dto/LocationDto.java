package ru.practicum.location.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @Min(-90)
    @Max(90)
    private Float lat;

    @Min(-180)
    @Max(180)
    private Float lon;
}
