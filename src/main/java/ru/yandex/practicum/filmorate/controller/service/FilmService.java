package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public Film create(Film film) {
        log.info("Создание фильма: {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Обновление фильма с ID: {}", film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        log.info("Получение всех фильмов");
        return filmStorage.getAll();
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) throws BadRequestException {
        log.info("Пользователь с ID {} убрал лайк у фильма с ID {}", userId, filmId);
        boolean success = filmStorage.deleteLike(filmId, userId);
        if (!success) {
            log.warn("Не удалось удалить лайк: фильм с ID {} не найден или лайка не было", filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение топ-{} популярных фильмов", count);
        return filmStorage.getPopularFilms(count);
    }

    public Optional<Film> getById(Long id) {
        log.info("Поиск фильма с ID: {}", id);
        return filmStorage.getById(id);
    }
}