package ru.practicum.compilation.service.pub;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> get(Boolean pinned, Long from, Long size);

    CompilationDto getById(Long compId);
}
