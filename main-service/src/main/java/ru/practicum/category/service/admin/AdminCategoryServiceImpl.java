package ru.practicum.category.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryMapper mapper = CategoryMapper.INSTANCE;
    private final CategoryRepository repository;

    @Override
    public CategoryDto save(NewCategoryDto newCategoryDto) {
        try {
            Category category = mapper.fromNewToModel(newCategoryDto);

            Category result = repository.save(category);
            return mapper.modelToDto(result);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void delete(Long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found", catId)));

        try {
            repository.deleteById(catId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public CategoryDto update(NewCategoryDto newCategoryDto, Long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found", catId)));

        try {
            category.setName(newCategoryDto.getName());

            return mapper.modelToDto(repository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Objects.requireNonNull(e.getMessage()));
        }
    }
}
