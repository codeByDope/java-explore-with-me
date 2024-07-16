package ru.practicum.category.service.pub;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> get(Long from, Long size);

    CategoryDto getById(Long catId);
}
