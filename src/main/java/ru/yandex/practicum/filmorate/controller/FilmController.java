package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);
    public static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private boolean isReleaseDateValid(LocalDate releaseDate) {
        return releaseDate != null && !releaseDate.isBefore(MIN_RELEASE_DATE);
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody @Valid Film film) {
        if (!isReleaseDateValid(film.getReleaseDate())) {
            log.error("Некорректная дата релиза: {}", film.getReleaseDate());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Некорректная дата релиза");
            error.put("message", "Дата релиза не может быть раньше 28 декабря 1895 года");
            return ResponseEntity.badRequest().body((Film) error);
        }

        Integer id = nextId.getAndIncrement();
        film.setId(id);
        films.put(id, film);
        log.info("Добавлен фильм с ID {}: {}", id, film);
        return ResponseEntity.status(201).body(film);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable Integer id, @RequestBody @Valid Film film) {
        if (!films.containsKey(id)) {
            log.error("Фильм с ID {} не найден для обновления", id);
            return ResponseEntity.notFound().build();
        }

        if (!isReleaseDateValid(film.getReleaseDate())) {
            log.error("Некорректная дата релиза при обновлении: {}", film.getReleaseDate());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Некорректная дата релиза");
            error.put("message", "Дата релиза не может быть раньше 28 декабря 1895 года");
            return ResponseEntity.badRequest().body((Film) error);
        }

        film.setId(id);
        films.put(id, film);
        log.info("Обновлён фильм с ID {}: {}", id, film);
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов (всего: {})", films.size());
        return ResponseEntity.ok(List.copyOf(films.values()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Integer id) {
        Film film = films.get(id);
        if (film == null) {
            log.warn("Попытка получения несуществующего фильма с ID {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Получен фильм с ID {}", id);
        return ResponseEntity.ok(film);
    }
}
