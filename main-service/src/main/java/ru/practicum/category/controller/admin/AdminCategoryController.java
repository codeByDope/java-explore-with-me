package ru.practicum.category.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.admin.AdminCategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/categories")
@Validated
public class AdminCategoryController {
    private final AdminCategoryService service;

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Создание новой категории: {}", newCategoryDto);
        CategoryDto result = service.save(newCategoryDto);

        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> delete(@PathVariable Long catId) {
        log.info("Запрос на удаление категории с ID {}", catId);
        service.delete(catId);

        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> update(@RequestBody @Valid NewCategoryDto newCategoryDto,
                                              @PathVariable Long catId) {
        log.info("Обновление категории с ID {}", catId);
        CategoryDto result = service.update(newCategoryDto, catId);

        return ResponseEntity.ok(result);
    }
}
