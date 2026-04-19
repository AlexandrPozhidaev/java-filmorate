package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.dbStorage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.db.dbStorage.DbUserStorage;
import ru.yandex.practicum.filmorate.storage.db.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@Import({DbUserStorage.class, DbFilmStorage.class, UserRowMapper.class, FilmRowMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final DbUserStorage userStorage;

    private final DbFilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void shouldCreateFilm() {
        Film newFilm = Film.builder()
                .name("New Film")
                .description("New Description")
                .duration(120L)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .mpaId(1L)
                .build();

        Film createdFilm = filmStorage.create(newFilm);

        assertThat(createdFilm).isNotNull();
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("New Film");
        assertThat(createdFilm.getDescription()).isEqualTo("New Description");
    }

    @Test
    void shouldFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getName()).isEqualTo("TestFilm1");
                    assertThat(film.getDuration()).isEqualTo(120);
                });
    }

    @Test
    void shouldReturnEmptyWhenFilmNotFound() {
        Optional<Film> filmOptional = filmStorage.getById(999L);
        assertThat(filmOptional).isEmpty();
    }

    @Test
    void shouldUpdateFilm() {
        Film existingFilm = filmStorage.getById(1L).orElse(null);
        assertThat(existingFilm).isNotNull();

        existingFilm.setName("Updated Film Name");
        existingFilm.setDuration(150L);

        Film updatedFilm = filmStorage.update(existingFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name");
        assertThat(updatedFilm.getDuration()).isEqualTo(150L);
    }

    @Test
    void shouldGetAllFilms() {
        List<Film> films = filmStorage.getAll();
        assertThat(films).hasSize(2);
        assertThat(films)
                .extracting("name")
                .containsExactly("TestFilm1", "TestFilm2");
    }

    @Test
    void shouldGetPopularFilms() {
        // Добавляем несколько лайков для фильма 1
        filmStorage.addLike(1L, 1L);
        filmStorage.addLike(1L, 2L);

        List<Film> popularFilms = filmStorage.getPopularFilms(1);
        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void shouldCreateUser() {
        User newUser = User.builder()
                .email("new@test.ru")
                .login("newuser")
                .name("New User")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User createdUser = userStorage.create(newUser);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("new@test.ru");
    }

    @Test
    void shouldFindUserById() {
        Optional<User> userOptional = userStorage.getById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(1L);
                    assertThat(user.getEmail()).isEqualTo("testUser1@test.ru");
                    assertThat(user.getLogin()).isEqualTo("testUser1");
                });
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> userOptional = userStorage.getById(999L);
        assertThat(userOptional).isEmpty();
    }

    @Test
    void shouldUpdateUser() {
        User existingUser = userStorage.getById(1L).orElse(null);
        assertThat(existingUser).isNotNull();

        existingUser.setName("Updated Name");
        existingUser.setEmail("updated@test.ru");

        User updatedUser = userStorage.update(existingUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.ru");
    }

    @Test
    void shouldGetAllUsers() {
        List<User> users = userStorage.getAll();
        assertThat(users).hasSize(3);
        assertThat(users)
                .extracting("login")
                .containsExactly("testUser1", "testUser2", "testUser3");
    }

    @Test
    void shouldDeleteUser() {
        userStorage.deleteUser(1L);

        Optional<User> deletedUser = userStorage.getById(1L);
        assertThat(deletedUser).isEmpty();

        // Проверяем, что пользователь действительно удалён из БД
        assertThatThrownBy(() -> userStorage.deleteUser(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User with id 1 not found");
    }

    @Test
    void shouldAddFriend() {
        // Добавляем друга пользователю с ID 1
        userStorage.addFriend(1L, 2L);

        // Получаем список друзей пользователя 1
        List<User> friends = userStorage.getFriends(1L);

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(2L);
    }

    @Test
    void shouldDeleteFriend() {
        // Сначала добавляем друга
        userStorage.addFriend(1L, 3L);

        // Затем удаляем
        userStorage.deleteFriend(1L, 3L);

        // Проверяем, что друг удалён
        List<User> friends = userStorage.getFriends(1L);
        assertThat(friends).isEmpty();
    }

    @Test
    void shouldGetFriends() {
        // Добавляем нескольких друзей пользователю 1
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);

        List<User> friends = userStorage.getFriends(1L);
        assertThat(friends).hasSize(2);
        assertThat(friends)
                .extracting("id")
                .containsExactlyInAnyOrder(2L, 3L); // Порядок может быть любым
    }

    @Test
    void shouldGetCommonFriends() {
        // Пользователь 1 дружит с 2 и 3
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);

        // Пользователь 4 дружит с 2 и 5
        userStorage.addFriend(4L, 2L);
        userStorage.addFriend(4L, 5L);

        // Общий друг — пользователь 2
        List<User> commonFriends = userStorage.getCommonFriends(1L, 4L);

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(2L);
    }
}