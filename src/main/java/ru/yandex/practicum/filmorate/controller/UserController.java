package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.mapper.UserMapper;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

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
                .collect(toList());
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        log.info("Запрошены данные пользователя с ID {}", id);
        User user = service.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        return mapper.toResponse(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id.toString(), friendId.toString());
        log.info("Пользователи с ID {} и {} стали друзьями", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriend(id.toString(), friendId.toString());
        log.info("Пользователи с ID {} и {} больше не друзья", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserResponse> getFriends(@PathVariable Long id) {
        log.info("Запрошен список друзей пользователя с ID {}", id);
        List<User> friends = service.getFriends(id);
        return friends.stream()
                .map(mapper::toResponse)
                .collect(toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрошен список общих друзей для пользователей с ID {} и {}", id, otherId);
        List<User> commonFriends = service.getCommonFriends(id.toString(), otherId.toString());
        return commonFriends.stream()
                .map(mapper::toResponse)
                .collect(toList());
    }
}
