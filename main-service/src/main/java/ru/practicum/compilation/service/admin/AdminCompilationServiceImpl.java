package ru.practicum.compilation.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.CompilationMapper;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository repository;
    private final CompilationMapper mapper = CompilationMapper.INSTANCE;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto save(NewCompilationDto compilation) {
        List<Event> events = new ArrayList<>();
        if (compilation.getEvents() != null) {
            events = findEventsByIds(compilation.getEvents());
        }
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        Compilation compilationForSave = mapper.fromNewToModel(compilation, events);
        Compilation result = repository.save(compilationForSave);

        return mapper.fromModelToDto(result);
    }

    @Override
    public void delete(Long compId) {
        Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found", compId)));
        repository.deleteById(compId);
    }

    @Override
    public CompilationDto update(UpdateCompilationDto compilation, Long compId) {
        Compilation compilationForUpdate = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%s was not found", compId)));

        if (compilation.getTitle() != null) {
            compilationForUpdate.setTitle(compilation.getTitle());
        }
        if (compilation.getPinned() != null) {
            compilationForUpdate.setPinned(compilation.getPinned());
        }
        if (compilation.getEvents() != null) {
            compilationForUpdate.setEvents(findEventsByIds(compilation.getEvents()));
        }

        Compilation result = repository.save(compilationForUpdate);

        return mapper.fromModelToDto(result);
    }

    private List<Event> findEventsByIds(List<Long> ids) {
        return ids.stream()
                .map(id -> eventRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(String.format(String.format("Event with id=%s was not found", id)))))
                .collect(Collectors.toList());

    }
}
