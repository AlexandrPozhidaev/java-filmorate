package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Начато создание фильма {}", film);
        try {
            validateReleaseDate(film.getReleaseDate());
            return service.create(film);
        } catch (BadRequestException e) {
            log.warn("Ошибка валидации даты релиза: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Начато обновление фильма {}", film);
        try {
            validateReleaseDate(film.getReleaseDate());
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
        return service.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Запрошен вывод всех фильмов");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Long id) {
        log.info("Запрошены данные фильма с ID {}", id);
        return service.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, id);
        service.addLike(id, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) throws BadRequestException {
        log.info("Пользователь с ID {} убрал лайк у фильма с ID {}", userId, id);
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список из {} популярных фильмов", count);
        if (count <= 0) {
            throw new IllegalArgumentException("Количество фильмов должно быть положительным");
        }
        return service.getPopularFilms(count);
    }

    private void validateReleaseDate(LocalDate releaseDate) throws BadRequestException {
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new BadRequestException(
                    "Дата релиза не может быть раньше 28/12/1895 года");
        }
    }
}