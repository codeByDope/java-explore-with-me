package ru.practicum.compilation.service.admin;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

public interface AdminCompilationService {
    CompilationDto save(NewCompilationDto compilation);

    void delete(Long compId);

    CompilationDto update(UpdateCompilationDto compilation, Long compId);
}
