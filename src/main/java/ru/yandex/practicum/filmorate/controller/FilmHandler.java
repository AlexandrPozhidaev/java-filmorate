package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class FilmHandler {

    private final HashMap<Long, Film> films = new HashMap<>();

    private Long generateId = 0L;



    public Film create(Film film) {
        film.setId(++generateId);
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {

        if (!films.containsKey(film.getId())) {
            throw new RuntimeException("Фильм с таким ID не найден");
        }

        films.put(film.getId(), film);
        return film;
    }

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}