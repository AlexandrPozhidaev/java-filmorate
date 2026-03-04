package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();

    // post
    @PostMapping
    public User createUser(@RequestBody User user) {
        try {
            log.info("Начало создания нового пользователя: " + user.getLogin());
            validateUser(user);
            user.setId(getNextUserId());
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                user.setName(user.getLogin());
                log.debug("Задано пустое имя пользователя - использован логин: " + user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь успешно создан, ID:" + user.getId());
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка валидации нового пользователя: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании нового пользователя: " + e.getMessage());
            throw e;
        }
    }

    private long getNextUserId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // put
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            log.info("Начало обновления пользователя с ID:" + id);

            if (!users.containsKey(id)) {
                log.warn("Пользователь с ID" + id + " не найден");
                throw new IllegalArgumentException("Пользователь с ID " + id + " не найден");
            }

            updatedUser.setId(id);
            validateUser(updatedUser);

            users.put(id, updatedUser);
            log.info("Пользователь с ID " + id + " обновлен");
            return updatedUser;
        } catch (ValidationException e) {
            log.error("Валидация не пройдена: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обновлении пользователя: " + e.getMessage());
            throw e;
        }
    }

    // get
    @GetMapping
    public Collection<User> findUsers() {
        return users.values();
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            log.debug("Ошибка: Email is empty or blank");
            throw new ValidationException("Email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            log.debug("Ошибка: Электронная почта должна содержать символ @: " + user.getEmail());
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            log.debug("Ошибка: Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.debug("Ошибка: Логин не может содержать пробелы: " + user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Ошибка: Дата рождения не может быть в будущем: " + user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        log.debug("Валидация пользователя пройдена успешно: " + user.getLogin());
    }

}