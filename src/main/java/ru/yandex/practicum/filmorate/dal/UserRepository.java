package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.debug("Сохраняем пользователя: id={}, login={}", user.getId(), user.getLogin());
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);
        return user;
    }

    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, new UserRowMapper(), id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public User update(User user) throws UserNotFoundException {
        validateUser(user);

        int rowsAffected = jdbc.update(
                "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        );

        if (rowsAffected == 0) {
            throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }

        return getById(user.getId()).orElseThrow(() ->
                new UserNotFoundException("Пользователь с ID " + user.getId() + " не найден"));
    }

    private void validateUser(User user) {
        if (user.getId() <= 0) {
            throw new ValidationException("ID пользователя должен быть положительным числом");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения не может быть пустой");
        }
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        if (user.getBirthday().isBefore(minDate)) {
            throw new ValidationException("Дата рождения должна быть после 1900 года");
        }
    }

    public List<User> getAllUsers() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        int rowsAffected = jdbc.update(sql, userId, friendId);

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Не удалось добавить дружбу между пользователями " +
                    userId + " и " + friendId);
        }
    }

    public boolean deleteFriend(Long userId, Long friendId) {
        String deleteFriendship = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        int rowsAffected = jdbc.update(deleteFriendship, userId, friendId);
        return rowsAffected > 0; // true — если запись была, false — если её не было
    }


    public List<User> getCommonFriends(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("ID пользователей не могут быть null");
        }

        String checkUser1 = "SELECT COUNT(*) FROM users WHERE id = ?";
        int user1Exists = jdbc.queryForObject(checkUser1, Integer.class, userId1);
        if (user1Exists == 0) {
            throw new NotFoundException("Пользователь с ID " + userId1 + " не найден");
        }

        String checkUser2 = "SELECT COUNT(*) FROM users WHERE id = ?";
        int user2Exists = jdbc.queryForObject(checkUser2, Integer.class, userId2);
        if (user2Exists == 0) {
            throw new NotFoundException("Пользователь с ID " + userId2 + " не найден");
        }

        String sql = """
                    SELECT u.id, u.name, u.email, u.login, u.birthday
                    FROM users u
                    WHERE u.id IN (
                        SELECT f1.friend_id
                        FROM friendships f1
                        WHERE f1.user_id = ?
                    )
                    AND u.id IN (
                        SELECT f2.friend_id
                        FROM friendships f2
                        WHERE f2.user_id = ?
                    )
                    ORDER BY u.name
                """;

        return jdbc.query(sql, mapper, userId1, userId2);
    }

    public List<User> getFriends(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }

        String checkUser = "SELECT COUNT(*) FROM users WHERE id = ?";
        int userExists = jdbc.queryForObject(checkUser, Integer.class, userId);
        if (userExists == 0) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        String sql = """
                    SELECT u.id, u.name, u.email, u.login, u.birthday
                    FROM users u
                    JOIN friendships f ON u.id = f.friend_id
                    WHERE f.user_id = ?
                    ORDER BY u.name
                """;

        return jdbc.query(sql, mapper, userId);
    }

    public void deleteAll() {
        jdbc.update("DELETE FROM users");
    }

    public boolean friendshipExists(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";


        try {
            Integer count = jdbc.queryForObject(sql, Integer.class, userId, friendId);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            log.warn("Ошибка при проверке дружбы между пользователями {} и {}: {}",
                    userId, friendId, e.getMessage());
            return false;
        }
    }
}