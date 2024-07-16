package ru.practicum.compilation.service.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository repository;
    private final CompilationMapper mapper = CompilationMapper.INSTANCE;

    @Override
    public List<CompilationDto> get(Boolean pinned, Long from, Long size) {
        int page = (int) (from/size);
        Pageable pageable = PageRequest.of(page, size.intValue());
        List<Compilation> compilations;

        if (pinned = null) {
            compilations = repository.findAll(pageable).getContent();
        } else if (pinned) {
            compilations = repository.findByPinnedTrue(pageable).getContent();
        } else {
            compilations = repository.findByPinnedFalse(pageable).getContent();
        }

        List<CompilationDto> result = mapper.fromModelListToDto(compilations);

        return result;
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found", compId)));
        return mapper.fromModelToDto(compilation);
    }
}
