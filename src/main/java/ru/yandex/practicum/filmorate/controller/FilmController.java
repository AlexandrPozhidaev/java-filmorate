package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.service.FilmService;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @PostMapping
    public ResponseEntity<FilmDto> create(@Valid @RequestBody FilmDto filmDto) {
        FilmDto createdFilm = service.create(filmDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping("/{id}")
    public FilmDto update(@Valid @RequestBody FilmDto dto) {
        log.info("Начато обновление фильма {}", dto);
        return service.update(dto);
    }

    @GetMapping
    public List<FilmDto> getAll() {
        log.info("Запрошен вывод всех фильмов");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public FilmDto getById(@PathVariable Long id) {
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
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список из {} популярных фильмов", count);
        if (count <= 0) {
            throw new IllegalArgumentException("Количество фильмов должно быть положительным");
        }
        return service.getPopularFilms(count);
    }
}