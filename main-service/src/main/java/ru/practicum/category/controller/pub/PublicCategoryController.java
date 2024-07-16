package ru.practicum.category.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.pub.PublicCategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/categories")
@Validated
public class PublicCategoryController {
    private final PublicCategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> get(@RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                 @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Запрошены категории");
        List<CategoryDto> result = service.get(from, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long catId) {
        log.info("Запрошена категория с ID {}", catId);
        CategoryDto result = service.getById(catId);

        return ResponseEntity.ok(result);
    }
}
