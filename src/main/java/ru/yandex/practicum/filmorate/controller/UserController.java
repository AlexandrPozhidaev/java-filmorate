package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/users")
@Valid
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody @Valid User user) {
        Integer id = nextId.getAndIncrement();
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.info("Добавлен пользователь с ID {}: {}", id, user);
        return ResponseEntity.status(201).body(user); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody @Valid User user) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с ID {} не найден для обновления", id);
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(id, user);
        log.info("Обновлён пользователь с ID {}: {}", id, user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей (всего: {})", users.size());
        return ResponseEntity.ok(List.copyOf(users.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Попытка получения несуществующего пользователя с ID {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Получен пользователь с ID {}", id);
        return ResponseEntity.ok(user);
    }
}
