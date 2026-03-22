package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);
    Film update(Film film);
    List<Film> getAll();
    boolean addLike(Long filmId, Long userId);
    boolean deleteLike(Long filmId, Long userId);
    List<Film> getPopularFilms(int count);
    Film getById(Long id);
    }
