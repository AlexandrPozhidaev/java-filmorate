package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor

public class UserController {

    private final UserHandler handler;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Начато создание пользователя {}", user);
        return handler.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Начато обновление пользователя {}", user);

        return handler.update(user);
    }

    @GetMapping
        public List<User> getAll() {
        log.info("Запрошен вывод всех пользователей");
        return handler.getAll();
    }
}