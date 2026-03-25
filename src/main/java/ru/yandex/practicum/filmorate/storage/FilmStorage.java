package ru.yandex.practicum.filmorate.storage;

import org.apache.coyote.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    void addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId) throws BadRequestException;

    List<Film> getPopularFilms(int count);

    Optional<Film> getById(Long id);
    }
