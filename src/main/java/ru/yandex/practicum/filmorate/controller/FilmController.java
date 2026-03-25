package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;
import ru.yandex.practicum.filmorate.model.response.FilmResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @PostMapping
    public FilmResponse create(@Valid @RequestBody FilmRequest filmRequest) {
        log.info("Начато создание фильма {}", filmRequest);
        return service.create(filmRequest);
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody FilmRequest filmRequest) {
        log.info("Начато обновление фильма {}", filmRequest);
        return service.update(filmRequest);
    }

    @GetMapping
    public List<FilmResponse> getAll() {
        log.info("Запрошен вывод всех фильмов");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public FilmResponse getById(@PathVariable Long id) {
        log.info("Запрошены данные фильма с ID {}", id);
        return service.getById(id);
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
    public List<FilmResponse> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список из {} популярных фильмов", count);
        if (count <= 0) {
            throw new IllegalArgumentException("Количество фильмов должно быть положительным");
        }
        return service.getPopularFilms(count);
    }
}