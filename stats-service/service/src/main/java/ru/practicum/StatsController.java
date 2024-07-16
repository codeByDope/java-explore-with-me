package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.ResponseDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid RequestDto requestBody) {
        log.info("Сохранение информации о посещении");
        service.hit(requestBody);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ResponseDto>> getStats(
            @RequestParam @NotNull @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime start,
            @RequestParam @NotNull @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime end,
            @RequestParam(defaultValue = "") List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Начало не может быть после конца");
        }

        if (uris.isEmpty() || (uris.size() == 1 && uris.get(0).isEmpty())) {
            uris = new ArrayList<>();
        }

        log.info("Запрос статистики по посещениям с {} до {}", start, end);
        return ResponseEntity.ok(service.getStats(start,end,uris,unique));
    }
}
