package ru.yandex.practicum.filmorate.controller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;

    public FilmDto create(FilmDto dto) {
        if (dto.getDuration() > 300) {
            throw new IllegalArgumentException("Продолжительность не может превышать 300 минут");
        }
        Film film = FilmMapper.toFilm(dto);
        log.info("Создание фильма: {}", film);
        Film createdFilm = filmRepository.create(film);
        return FilmMapper.mapToFilmDto(createdFilm);
    }


    public FilmDto update(FilmDto dto) {
        validateFilmDto(dto);

        Film film = FilmMapper.toFilm(dto);
        log.info("Обновление фильма с ID: {}", film.getId());

        Film updatedFilm = filmRepository.update(film);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    private void validateFilmDto(FilmDto dto) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (dto.getDescription() != null && dto.getDescription().length() > 200) {
            throw new ValidationException("Описание не может превышать 200 символов");
        }
        if (dto.getGenre() == null || dto.getGenre().isEmpty()) {
            throw new ValidationException("Фильм должен иметь хотя бы один жанр");
        }
        for (GenreDto genre : dto.getGenre()) {
            if (!genreRepository.findById(genre.getId()).isPresent()) {
                throw new ValidationException("Жанр с ID " + genre.getId() + " не существует");
            }
        }
    }

    public List<FilmDto> getAll() {
        log.info("Получение всех фильмов");
        return filmRepository.getAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(Long id) {
        log.info("Поиск фильма с ID: {}", id);
        Film film = filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
        return FilmMapper.mapToFilmDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
        filmRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));

        filmRepository.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) throws BadRequestException {
        log.info("Пользователь с ID {} убрал лайк у фильма с ID {}", userId, filmId);
        Film film = filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException(
                    "Пользователь с ID " + userId + " не ставил лайк этому фильму"
            );
        }

        film.getLikes().remove(userId);
        }

    public List<FilmDto> getPopularFilms(int count) {
        log.info("Получение топ-{} популярных фильмов", count);
        return filmRepository.getPopularFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }


}
