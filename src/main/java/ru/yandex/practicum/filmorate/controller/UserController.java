package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.UserService;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserResponse create(@Validated(User.OnCreate.class) @RequestBody UserRequest request) {
        log.info("Начато создание пользователя {}", request);
        return service.create(request);
    }

    @PutMapping
    public UserResponse update(@Validated(User.OnUpdate.class) @RequestBody UserRequest request) {
        log.info("Начато обновление пользователя {}", request);
        return service.update(request);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        log.info("Запрошен вывод всех пользователей");
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        log.info("Запрошены данные пользователя с ID {}", id);
        return service.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление в друзья: пользователь {} добавляет пользователя {}", id, friendId);
        service.addFriend(id, friendId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<ErrorResponse> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        try {
            service.deleteFriend(id, friendId);
            log.info("Пользователи с ID {} и {} больше не друзья", id, friendId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (NotFoundException e) {
            log.warn("Ошибка при удалении друга: {}", e.getMessage());
            ErrorResponse error = new ErrorResponse(
                    e.getMessage(),
                    404,
                    Collections.singletonList("Ресурс не найден")
            );
            return ResponseEntity.status(404).body(error);
        }
    }

    @GetMapping("/{id}/friends")
    public List<UserResponse> getFriends(@PathVariable Long id) {
        log.info("Запрошен список друзей пользователя с ID {}", id);
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserResponse> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрошен список общих друзей для пользователей с ID {} и {}", id, otherId);
        return service.getCommonFriends(id, otherId);
    }
}
