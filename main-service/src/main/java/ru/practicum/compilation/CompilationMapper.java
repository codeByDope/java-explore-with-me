package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;

import java.util.List;

@Mapper
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "events", source = "newEvents")
    Compilation fromNewToModel(NewCompilationDto compilation, List<Event> newEvents);

    CompilationDto fromModelToDto(Compilation compilation);

    List<CompilationDto> fromModelListToDto(List<Compilation> compilations);
}
