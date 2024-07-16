package ru.practicum.dto;

import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    private String app;
    private String uri;
    private Long hits;
}

