package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.dbStorage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.dbStorage.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.db.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mapper.UserRowMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({DbUserStorage.class, DbFilmStorage.class, UserRowMapper.class, FilmRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final DbUserStorage userStorage;

    private final DbFilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

}