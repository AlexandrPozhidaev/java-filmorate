package ru.yandex.practicum.filmorate.dal;

public interface FriendRepository {

    void addFriend(Long uderId, Long friendId);

    void removeFriend(Long uderId, Long friendId);
}
