package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface AdminUserService {
    List<UserDto> get(List<Long> ids, Long from, Long start);

    UserDto save(NewUserDto newUser);

    void delete(Long userId);
}
