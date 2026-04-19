package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;
import ru.yandex.practicum.filmorate.model.response.FilmResponse;
import ru.yandex.practicum.filmorate.controller.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponse createFilm(@RequestBody FilmRequest filmRequest) {
        return filmService.create(filmRequest);
    }

    @PutMapping
    public FilmResponse updateFilm(@RequestBody FilmRequest filmRequest) {
        return filmService.update(filmRequest);
    }

    @GetMapping
    public List<FilmResponse> getAllFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public FilmResponse getFilmById(@PathVariable Long id) {
        return filmService.getById(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmResponse> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            count = 10;
        }
        return filmService.getPopularFilms(count);
    }
}
