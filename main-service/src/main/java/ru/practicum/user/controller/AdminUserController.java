package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.AdminUserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {
    private final AdminUserService service;

    @GetMapping
    public ResponseEntity<List<UserDto>> get(@RequestParam List<Long> ids,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                             @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Были запрошены пользователи с ID {}", ids);
        List<UserDto> result = service.get(ids, from, size);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody NewUserDto newUser) {
        log.info("Создание нового пользователя: {}", newUser);
        UserDto result = service.save(newUser);

        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        log.info("Удаление пользователя с ID {}", userId);
        service.delete(userId);

        return ResponseEntity.status(204).build();
    }
}
