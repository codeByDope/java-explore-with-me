package ru.practicum.compilation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.admin.AdminCompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {
    private final AdminCompilationService service;

    @PostMapping
    public ResponseEntity<CompilationDto> save(@Valid @RequestBody NewCompilationDto compilation) {
        log.info("Сохранение новой подборки {}", compilation.getTitle());
        CompilationDto result = service.save(compilation);
        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Void> delete(@PathVariable Long compId) {
        log.info("Удаление подборки с ID {}", compId);
        service.delete(compId);
        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> update(@RequestBody @Valid UpdateCompilationDto updateCompilation,
                                                 @PathVariable Long compId) {
        log.info("Обновление подборки с ID {}", compId);
        CompilationDto result = service.update(updateCompilation, compId);
        return ResponseEntity.ok(result);
    }
}
