package ru.practicum.service;

import ru.practicum.dto.RequestDto;
import ru.practicum.dto.ResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void hit(RequestDto requestDto);

    List<ResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
