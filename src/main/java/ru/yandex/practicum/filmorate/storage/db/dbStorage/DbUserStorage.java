package ru.yandex.practicum.filmorate.storage.db.dbStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.mapper.UserRowMapper;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@Component
@RequiredArgsConstructor
public class DbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

     @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()));

        Long userId = jdbcTemplate.queryForObject(
                ("SELECT LAST_INSERT_ID()"), Long.class);
        user.setId(userId);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, " +
                "birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());
        return user;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.of(user);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        if (rowsAffected == 0) {
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден");
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT IGNORE INTO user_friends (id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM user_friends WHERE id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN user_friends uf1 ON u.id = uf1.friend_id " +
                "JOIN user_friends uf2 ON u.id = uf2.friend_id " +
                "WHERE uf1.id = ? AND uf2.id = ? " +
                "AND uf1.friend_id = uf2.friend_id";
        return jdbcTemplate.query(sql, userRowMapper, userId1, userId2);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN user_friends uf ON u.id = uf.friend_id " +
                "WHERE uf.id = ? ORDER BY u.id";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }
}
