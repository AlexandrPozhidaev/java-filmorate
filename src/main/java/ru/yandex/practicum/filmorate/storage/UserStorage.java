package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);
    User update(User user);
    List<User> getAll();
    Optional<User> getById(Long id);
    void deleteUser(Long id);
    void addFriend(Long userId, Long friendId);
    void deleteFriend(Long userId, Long friendId);
    List<User> getCommonFriends(Long userId1, Long userId2);
    List<User> getFriends(Long userId);
}
