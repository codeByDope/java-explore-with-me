package ru.practicum.compilation.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.pub.PublicCompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/compilations")
@Validated
public class PublicCompilationController {
    private final PublicCompilationService service;

    @GetMapping
    ResponseEntity<List<CompilationDto>> get(@RequestParam(required = false) Boolean pinned,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                             @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Были запрошены подборки, закреплены?: {}", pinned);
        return ResponseEntity.ok(service.get(pinned, from, size));
    }

    @GetMapping("/{compId}")
    ResponseEntity<CompilationDto> getById(@PathVariable Long compId) {
        log.info("Была запрошена подборка с ID {}", compId);
        return ResponseEntity.ok(service.getById(compId));
    }
}
