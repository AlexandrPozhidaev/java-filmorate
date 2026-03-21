package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);

        if (film == null) {
            log.warn("Фильм с ID {} не найден", filmId);
            throw new IllegalArgumentException("Фильм не найден");
        }

        Set<Long> likes = film.getLikes();

        if (likes.contains(userId)) {
            log.info("Пользователь с ID {} уже поставил лайк фильму с ID {}", userId, filmId);
            return;
        }

        likes.add(userId);
        filmStorage.update(film);

        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);

        if (film == null) {
            log.warn("Фильм с ID {} не найден", filmId);
            throw new IllegalArgumentException("Фильм не найден");
        }

        Set<Long> likes = film.getLikes();

        if (!likes.contains(userId)) {
            log.info("Пользователь с ID {} не ставил лайк фильму с ID {}", userId, filmId);
            return;
        }

        likes.remove(userId);
        filmStorage.update(film);

        log.info("Пользователь с ID {} убрал лайк у фильма с ID {}", userId, filmId);
    }

       public List<Film> get10PopularFilms(int count) {
        if (count <= 0) {
            count = 10; // значение по умолчанию
        }

        List<Film> allFilms = filmStorage.getAll();

        return allFilms.stream()
                .sorted((f1, f2) -> {
                    int likesComparison = Integer.compare(
                            f2.getLikes().size(),
                            f1.getLikes().size()
                    );
                    if (likesComparison != 0) {
                        return likesComparison;
                    }

                    return Long.compare(f2.getId(), f1.getId());
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    public Optional<Film> getById(Long id) {
        return Optional.ofNullable(filmStorage.getById(id));
    }
}