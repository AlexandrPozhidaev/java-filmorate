package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID {} добавляет в друзья пользователя с ID {}", userId, friendId);

        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        user.getFriends().add(friend);
        friend.getFriends().add(user);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID {} удаляет из друзей пользователя с ID {}", userId, friendId);

        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = userStorage.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        user.getFriends().removeIf(f -> f.getId().equals(friendId));
        friend.getFriends().removeIf(f -> f.getId().equals(userId));

        userStorage.update(user);
        userStorage.update(friend);
    }

     public List<User> getCommonFriends(Long userId1, Long userId2) {
        log.info("Поиск общих друзей для пользователей с ID: {} и {}", userId1, userId2);

        User user1 = userStorage.getById(userId1)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId1 + " не найден"));
        User user2 = userStorage.getById(userId2)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId2 + " не найден"));

        Set<Long> friendsOfUser1Ids = user1.getFriends().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<Long> friendsOfUser2Ids = user2.getFriends().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Set<Long> commonFriendIds = friendsOfUser1Ids.stream()
                .filter(friendsOfUser2Ids::contains)
                .collect(Collectors.toSet());

        return commonFriendIds.stream()
                .map(id -> userStorage.getById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<User> getById(Long id) {
        log.info("Поиск пользователя с ID: {}", id);
        return userStorage.getById(id);
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));

        log.debug("Загружаем друзей для пользователя с ID: {}", id);
        return user.getFriends().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}