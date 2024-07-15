package ru.practicum;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.ResponseDto;
import ru.practicum.model.Stats;

@Mapper
public interface StatsMapper {
    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    Stats requestToModel(RequestDto request);

    @Mapping(target = "hits", ignore = true)
    ResponseDto modelToResponse(Stats stats);
}