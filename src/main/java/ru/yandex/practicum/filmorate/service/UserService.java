package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto create(UserDto dto) {
        log.info("Создание пользователя с логином: {}", dto.getLogin());
        if (dto.getLogin() == null || dto.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (dto.getLogin().contains(" ")) {
            throw new IllegalArgumentException("Логин не должен содержать пробелы");
        }
        User user = UserMapper.toUser(dto);

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }

        User createdUser = userRepository.createUser(user);
        log.debug("Пользователь создан, ID: {}, логин: {}", createdUser.getId(), createdUser.getLogin());
        return UserMapper.toUserDto(createdUser);
    }

    public UserDto update(UserDto dto) throws UserNotFoundException {
        log.info("Начато обновление пользователя {}", dto);
        if (dto.getId() == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        User existingUser = userRepository.getById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + dto.getId() + " не найден"));

        applyUserUpdate(existingUser, dto);

        User updatedUser = userRepository.update(existingUser);
        log.info("Пользователь с ID {} успешно обновлён, новый логин: {}", updatedUser.getId(), updatedUser.getLogin());
        return UserMapper.toUserDto(updatedUser);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    public void addFriend(Long userId, Long friendId) throws UserNotFoundException {
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("ID пользователей не могут быть null");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь не может добавить себя в друзья");
        }

        User user = userRepository.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = userRepository.getById(friendId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + friendId + " не найден"));

        log.info("Пользователь с ID {} добавляет в друзья пользователя с ID {}", userId, friendId);
        userRepository.addFriend(userId, friendId);
    }


    public boolean deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь с ID {} удаляет из друзей пользователя с ID {}", userId, friendId);
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("ID пользователей не могут быть null");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь не может удалить себя из друзей");
        }

        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        userRepository.getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        boolean friendshipExisted = userRepository.deleteFriend(userId, friendId);
        log.info("Друг успешно удалён: {} → {}, дружба существовала: {}", userId, friendId, friendshipExisted);
        return friendshipExisted;
    }

    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
        log.info("Поиск общих друзей для пользователей с ID: {} и {}", userId1, userId2);
        List<User> commonFriends = userRepository.getCommonFriends(userId1, userId2);
        return commonFriends.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getFriends(Long id) {
        log.debug("Загружаем друзей для пользователя с ID: {}", id);
        List<User> friends = userRepository.getFriends(id);
        return friends.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void applyUserUpdate(User existingUser, UserDto dto) {
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getLogin() != null && !dto.getLogin().trim().isEmpty()) {
            existingUser.setLogin(dto.getLogin());
        }
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            existingUser.setName(dto.getName());
        }
        if (dto.getBirthday() != null) {
            existingUser.setBirthday(dto.getBirthday());
        }
    }
}
