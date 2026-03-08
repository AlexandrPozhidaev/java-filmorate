package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class UserHandler {

    private final HashMap<Long, User> users = new HashMap<>();

    private Long generateId = 0L;

    public User create(@Valid User user) {

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }

        user.setId(++generateId);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    public User update(@Valid User user) {
        if (users.containsKey(user.getId())) {
            throw new RuntimeException("Пользователь с таким ID не найден");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя не задано - использован логин");
        }

        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }

    public List<User> getAll() {
        log.info("Вывод списка всех пользователей");
        return new ArrayList<>(users.values());
    }
}