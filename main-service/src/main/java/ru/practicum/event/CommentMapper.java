package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.model.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    CommentDto toDto(Comment comment);

    Comment toModel(CommentDto commentDto);

    List<CommentDto> toDtoList(List<Comment> comments);
}