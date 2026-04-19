package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.mapper.UserMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserServiceInt{

    private final UserStorage userStorage;
    private final UserMapper mapper;

    @Override
    public UserResponse create(UserRequest request) {
        User user = mapper.toUser(request);
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
        User createdUser = userStorage.create(user);
        return mapper.toResponse(createdUser);
    }

    @Override
    public UserResponse update(UserRequest request) {
        log.info("Начато обновление пользователя {}", request);

        User existingUser = userStorage.getById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + request.getId() + " не найден"));

        applyUserUpdate(existingUser, request);

        User updatedUser = userStorage.update(existingUser);
        log.info("Пользователь с ID {} успешно обновлён", updatedUser.getId());
        return mapper.toResponse(updatedUser);
    }

    @Override
    public List<UserResponse> getAll() {
        return userStorage.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponse> getById(Long id) {
        return userStorage.getById(id)
                .map(mapper::toResponse);
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        userStorage.deleteUser(id);
        log.info("Пользователь с ID {} успешно удалён", id);
    }

    @Override
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

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID {} удаляет из друзей пользователя с ID {}", userId, friendId);
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

    @Override
    public List<UserResponse> getCommonFriends(Long userId1, Long userId2) {
        log.info("Поиск общих друзей для пользователей с ID: {} и {}", userId1, userId2);
        List<User> commonFriends = userStorage.getCommonFriends(userId1, userId2);
        return commonFriends.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getFriends(Long id) {
        log.debug("Загружаем друзей для пользователя с ID: {}", id);
        List<User> friends = userStorage.getFriends(id);
        return friends.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private void applyUserUpdate(User existingUser, UserRequest request) {
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getLogin() != null) {
            existingUser.setLogin(request.getLogin());
        }
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            existingUser.setName(request.getName());
        } else {
            existingUser.setName(existingUser.getLogin());
        }
        if (request.getBirthday() != null) {
            existingUser.setBirthday(request.getBirthday());
        }
    }
}
