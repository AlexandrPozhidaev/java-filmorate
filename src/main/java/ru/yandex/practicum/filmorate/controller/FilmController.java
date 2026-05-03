package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid FilmDto dto) {
        try {
            FilmDto created = service.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ValidationException ex) {
            if (ex.getMessage().contains("MPA с ID") && ex.getMessage().contains("не существует")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(ex.getMessage(), 404));
            }
            throw ex;
        }
    }

    @PutMapping
    public ResponseEntity<FilmDto> updateFilm(@RequestBody @Valid FilmDto dto) throws BadRequestException {
        if (dto.getId() == null) {
            throw new BadRequestException("ID фильма обязателен для обновления");
        }

        FilmDto updatedFilm = service.update(dto);
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping
    public ResponseEntity<List<FilmDto>> getAll() {
        log.info("Запрошен вывод всех фильмов");
        List<FilmDto> films = service.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(films);
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