package ru.yandex.practicum.filmorate.controller.service;

import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserServiceInt {

    UserResponse create(UserRequest userRequest);

    UserResponse update(UserRequest userRequest);

    List<UserResponse> getAll();

    Optional<UserResponse> getById(Long id);

    void deleteUser(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<UserResponse> getCommonFriends(Long userId1, Long userId2);

    List<UserResponse> getFriends(Long id);

}
