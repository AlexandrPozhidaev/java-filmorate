package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
    void testValidFilm() {
        Film film = new Film(null, "Наименование", "Описание",
                LocalDate.of(2025, 1, 1), 120);

        assertTrue(isValidFilm(film), "Валидный фильм должен проходить проверку");
    }

    @Test
    void testEmptyName() {
        Film film = new Film(null, "", "Описание",
                LocalDate.of(2020, 1, 1), 120);

        assertFalse(isValidFilm(film), "Фильм с пустым названием не должен проходить проверку");
    }

    @Test
    void testNullReleaseDate() {
        Film film = new Film(null, "Наименование", "Описание", null, 120);

        assertFalse(isValidFilm(film), "Фильм с null датой выхода не должен проходить проверку");
    }

    @Test
    void testNegativeDuration() {
        Film film = new Film(null, "Наименование", "Описание",
                LocalDate.of(2020, 1, 1), -10);

        assertFalse(isValidFilm(film), "Фильм с отрицательной длительностью не должен проходить проверку");
    }

    @Test
    void testLongName() {
        String longDescription = "A".repeat(201);
        Film film = new Film(null, "Наименование", longDescription,
                LocalDate.of(2020, 1, 1), 120);

        assertFalse(isValidFilm(film), "Фильм с описанием длиннее 200 символов не должен проходить проверку");
    }

    @Test
    void testFutureReleaseDate() {
        Film film = new Film(null, "Наименование", "Описание",
                LocalDate.now().plusDays(1), 120);

        assertFalse(isValidFilm(film), "Фильм с будущей датой выхода не должен проходить проверку");
    }

    private boolean isValidFilm(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            return false;
        }
        if (film.getDescription().length() > 200) {
            return false;
        }
        if (film.getReleaseDate() == null) {
            return false;
        }
        if (film.getReleaseDate().isAfter(LocalDate.now())) {
            return false;
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            return false;
        }
        return true;
    }

    @Test
    void testValidUser() {
        User user = new User(null, "test@mail.ru", "test",
                "John Doe", LocalDate.of(1990, 1, 1));

        assertTrue(isValidUser(user), "Валидный пользователь должен проходить проверку");
    }

    @Test
    void testInvalidEmail() {
        User user = new User(null, "invalid-email", "login",
                "Name", LocalDate.of(1990, 1, 1));

        assertFalse(isValidUser(user), "Пользователь с некорректным email не должен проходить проверку");
    }

    @Test
    void testEmptyEmail() {
        User user = new User(null, "", "test", "Tester",
                LocalDate.of(1990, 1, 1));

        assertFalse(isValidUser(user), "Пользователь с пустым email не должен проходить проверку");
    }

    @Test
    void testLoginWithSpaces() {
        User user = new User(null, "user@mail.ru", "login with spaces",
                "Name", LocalDate.of(1990, 1, 1));

        assertFalse(isValidUser(user), "Пользователь с пробелами в логине не должен проходить проверку");
    }

    @Test
    void testFutureBirthday() {
        User user = new User(null, "user@mail.ru", "login",
                "Name", LocalDate.now().plusDays(1));

        assertFalse(isValidUser(user), "Пользователь с будущей датой рождения не должен проходить проверку");
    }

    @Test
    void testNullBirthday() {
        User user = new User(null, "user@mail.ru", "login", "Name", null);

        assertFalse(isValidUser(user), "Пользователь с null датой рождения не должен проходить проверку");
    }

    private boolean isValidUser(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        if (!user.getEmail().contains("@")) {
            return false;
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty() ||
                user.getLogin().contains(" ")) {
            return false;
        }
        if (user.getBirthday() == null) {
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }
}