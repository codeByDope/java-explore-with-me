package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository repository;
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public List<UserDto> get(List<Long> ids, Long from, Long size) {
        int page = (int) (from / size);
        Pageable pageable = PageRequest.of(page, size.intValue());

        List<User> result;

        if (ids == null) {
            result = repository.findAll(pageable).getContent();
        } else {
            result = repository.findByIdIn(ids, pageable).getContent();
        }

        return mapper.modelListToDto(result);
    }

    @Override
    public UserDto save(NewUserDto newUser) {
        User user = mapper.newToModel(newUser);

        UserDto result = mapper.modelToDto(repository.save(user));

        return result;
    }

    @Override
    public void delete(Long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s was not found", userId)));

        repository.deleteById(userId);
    }
}
