package ru.practicum.category;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto modelToDto(Category category);

    List<CategoryDto> modelListToDto(List<Category> category);

    Category fromNewToModel(NewCategoryDto newCategoryDto);
}
