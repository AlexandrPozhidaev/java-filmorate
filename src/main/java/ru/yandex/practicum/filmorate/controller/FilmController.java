package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService handler;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Начато создание фильма {}", film);
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new RuntimeException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return handler.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Начато обновление фильма {}", film);
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new RuntimeException(
                    "Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return handler.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Запрошен вывод всех фильмов");
        return handler.getAll();
    }
}