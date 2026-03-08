package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class UserHandler {

    private final HashMap<Long, User> users = new HashMap<>();

    private Long generateId = 0L;

    public User create(@Valid User user) {

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email должен содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(++generateId);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    public User update(@Valid User user) {
        if (!users.containsKey(user.getId())) {
            throw new RuntimeException("Пользователь с таким ID не найден");
        }

        User existingUser = users.get(user.getId());
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            existingUser.setLogin(user.getLogin());
        }
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            existingUser.setName(user.getName());
        } else {
            existingUser.setName(existingUser.getLogin());
        }
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }

        log.info("Пользователь обновлен: {}", existingUser);
        return existingUser;
    }

    public List<User> getAll() {
        log.info("Вывод списка всех пользователей");
        return new ArrayList<>(users.values());
    }
}