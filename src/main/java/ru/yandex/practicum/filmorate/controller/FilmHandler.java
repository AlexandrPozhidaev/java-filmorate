package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class FilmHandler {

    private final HashMap<Long, Film> films = new HashMap<>();

    private Long generateId = 0L;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public Film create(@Valid Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new RuntimeException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        film.setId(++generateId);
        films.put(film.getId(), film);
        return film;
    }

    public Film update(@Valid Film film) {

        if (!films.containsKey(film.getId())) {
            throw new RuntimeException("Фильм с таким ID не найден");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new RuntimeException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        films.put(film.getId(), film);
        return film;
    }

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}