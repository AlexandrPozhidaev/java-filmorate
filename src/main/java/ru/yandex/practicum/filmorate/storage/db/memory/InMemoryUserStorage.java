package ru.yandex.practicum.filmorate.storage.db.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long generateId = 0L;

    @Override
    public User create(User user) {
        user.setId(++generateId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new RuntimeException("Пользователь с таким ID не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }

        // Удаляем пользователя из списков друзей других пользователей
        for (User user : users.values()) {
            if (user.getFriends() != null) {
                user.getFriends().remove(id);
            }
        }
        users.remove(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        // Инициализируем множества друзей, если они null
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        // Проверка на дублирование
        if (!user.getFriends().contains(friendId)) {
            user.getFriends().add(friendId);
        }
        if (!friend.getFriends().contains(userId)) {
            friend.getFriends().add(userId);
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        User friend = getById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден"));

        // Инициализируем множества, если null
        if (user.getFriends() == null) user.setFriends(new HashSet<>());
        if (friend.getFriends() == null) friend.setFriends(new HashSet<>());

        boolean userHadFriend = user.getFriends().contains(friendId);
        boolean friendHadUser = friend.getFriends().contains(userId);

        // Двустороннее удаление
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = getById(userId1)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId1 + " не найден"));
        User user2 = getById(userId2)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId2 + " не найден"));

        Set<Long> friendsOfUser1Ids = user1.getFriends() != null ? user1.getFriends() : new HashSet<>();
        Set<Long> friendsOfUser2Ids = user2.getFriends() != null ? user2.getFriends() : new HashSet<>();

// Находим общие ID друзей
        Set<Long> commonFriendIds = new HashSet<>(friendsOfUser1Ids);
        commonFriendIds.retainAll(friendsOfUser2Ids);

// Преобразуем ID в объекты User
        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : commonFriendIds) {
            Optional<User> friend = getById(friendId);
            if (friend.isPresent()) {
                commonFriends.add(friend.get());
            }
        }
        return commonFriends;
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID" + userId + "не найден"));

        if (user.getFriends() == null) {
            return Collections.emptyList();
        }

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            Optional<User> friend = getById(friendId);
            if (friend.isPresent()) {
                friends.add(friend.get());
            }
        }
        return friends;
    }
}
