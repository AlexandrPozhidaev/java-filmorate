package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;
import ru.yandex.practicum.filmorate.model.response.FilmResponse;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper mapper;

    public FilmResponse create(FilmRequest filmRequest) {
        Film film = mapper.toFilm(filmRequest);
        log.info("Создание фильма: {}", film);
        Film createdFilm = filmStorage.create(film);
        return mapper.toResponse(createdFilm);
    }

    public FilmResponse update(FilmRequest filmRequest) {
        Film film = mapper.toFilm(filmRequest);
        log.info("Обновление фильма с ID: {}", film.getId());
        Film updatedFilm = filmStorage.update(film);
        return mapper.toResponse(updatedFilm);
    }

    public List<FilmResponse> getAll() {
        log.info("Получение всех фильмов");
        return filmStorage.getAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
        userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

            filmStorage.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Пользователь с ID {} убрал лайк у фильма с ID {}", userId, filmId);
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException(
                    "Пользователь с ID " + userId + " не ставил лайк этому фильму"
            );
        }
        film.getLikes().remove(userId);
        }

    public List<FilmResponse> getPopularFilms(int count) {
        log.info("Получение топ-{} популярных фильмов", count);
        return filmStorage.getPopularFilms(count).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public FilmResponse getById(Long id) {
        log.info("Поиск фильма с ID: {}", id);
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
        return mapper.toResponse(film);
    }
}
