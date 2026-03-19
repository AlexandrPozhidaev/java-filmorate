package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    public UserResponse create(@Validated(User.OnCreate.class) @RequestBody UserRequest request) {
        log.info("Начато создание пользователя {}", request);
        return mapper.toResponse(service.create(mapper.toUser(request)));
    }

    @PutMapping
    public UserResponse update(@Validated(User.OnUpdate.class) @RequestBody UserRequest request) {
        log.info("Начато обновление пользователя {}", request);
        return mapper.toResponse(service.update(mapper.toUser(request)));
    }

    @GetMapping
    public List<UserResponse> getAll() {
        log.info("Запрошен вывод всех пользователей");
        return service.getAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final String id, @PathVariable final friendId) {
        service.addFriend(id, friendId);
    }

}