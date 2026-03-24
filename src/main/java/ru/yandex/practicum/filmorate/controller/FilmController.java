package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;
    private final FilmMapper filmMapper;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film create(@Valid @RequestBody FilmRequest filmRequest) {
        log.info("Начато создание фильма {}", filmRequest);
        Film film = filmMapper.toFilm(filmRequest);
        return service.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Начато обновление фильма {}", film);
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
}