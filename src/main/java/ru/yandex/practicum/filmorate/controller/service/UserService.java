package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        if (!userStorage.getById(user.getId()).isPresent()) {
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID {} добавляет в друзья пользователя с ID {}", userId, friendId);
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("ID пользователей не могут быть null");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь не может добавить себя в друзья");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID {} удаляет из друзей пользователя с ID {}", userId, friendId);

        // Валидация
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("ID пользователей не могут быть null");
        }
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        userStorage.deleteFriend(userId, friendId);
        log.info("Друг успешно удалён: {} → {}", userId, friendId);
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        log.info("Поиск общих друзей для пользователей с ID: {} и {}", userId1, userId2);
        return userStorage.getCommonFriends(userId1, userId2);
    }

    public Optional<User> getById(Long id) {
        log.info("Поиск пользователя с ID: {}", id);
        return userStorage.getById(id);
    }

    public List<User> getFriends(Long id) {
        log.debug("Загружаем друзей для пользователя с ID: {}", id);
        return userStorage.getFriends(id);
    }
}
