package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    // post
    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        try {
            log.info("Начало создания нового фильма: " + film.getName());
            validateFilm(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм успешно создан, ID:" + film.getId());
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка валидации нового фильма: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при создании нового фильма: " + e.getMessage(), e);
            throw e;
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // put
    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable Long id, @RequestBody Film updatedFilm) {
        try {
            log.info("Начало обновления фильма с ID:" + id);

            if (!films.containsKey(id)) {
                log.warn("Фильм с ID " + id + " не найден");
                throw new IllegalArgumentException("Фильм с ID " + id + " не найден");
            }

            updatedFilm.setId(id);
            validateFilm(updatedFilm);

            films.put(id, updatedFilm);
            log.info("Фильм с ID " + id + " обновлен");
            return updatedFilm;
        } catch (ValidationException e) {
            log.error("Валидация не пройдена: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обновлении фильма: " + e.getMessage(), e);
            throw e;
        }
    }

    // get
    @GetMapping
    public Collection<Film> findFilms() {
        log.info("Фильмов всего: {}", films.size());
        return films.values();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            log.debug("Ошибка: Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.debug("Ошибка: Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.debug("Ошибка: Дата релиза - не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза - не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            log.debug("Ошибка: Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        log.debug("Валидация фильма пройдена успешно: " + film.getName());
    }
}