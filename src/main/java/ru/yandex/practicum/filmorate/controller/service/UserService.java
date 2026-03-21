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

    public void addFriend(String id, String friendId) {
        User user = userStorage.getById(Long.valueOf(id));
        User friend = userStorage.getById(Long.valueOf(friendId));
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public void deleteFriend(String id, String friendId) {
        User user = userStorage.getById(Long.valueOf(id));
        User friend = userStorage.getById(Long.valueOf(friendId));

        user.getFriends().remove(Long.valueOf(friendId));
        friend.getFriends().remove(Long.valueOf(id));

        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<User> getCommonFriends(String id1, String id2) {
        User user1 = userStorage.getById(Long.valueOf(id1));
        User user2 = userStorage.getById(Long.valueOf(id2));

        Set<Long> friendsOfUser1 = user1.getFriends();
        Set<Long> friendsOfUser2 = user2.getFriends();

        Set<Long> commonFriendsIds = friendsOfUser1.stream()
                .filter(friendsOfUser2::contains)
                .collect(Collectors.toSet());

        return commonFriendsIds.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public Optional<User> getById(Long id) {
        return Optional.ofNullable(userStorage.getById(id));
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user.getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}