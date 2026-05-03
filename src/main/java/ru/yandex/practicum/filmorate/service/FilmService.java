package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final GenreRowMapper genreRowMapper;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final JdbcTemplate jdbc;

    public FilmDto create(FilmDto dto) throws ValidationException {
        validateFilmDto(dto);

        Mpa mpa = mpaRepository.getMpaById(dto.getMpa().getId())
                .orElseThrow(() -> new ValidationException(
                        "MPA с ID " + dto.getMpa().getId() + " не существует"));

        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            Set<Long> genreIds = dto.getGenres().stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet());

            List<Genre> existingGenres = genreRepository.getGenresByIds(genreIds);
            if (existingGenres.size() != genreIds.size()) {
                throw new NotFoundException("Один или несколько жанров с ID " + genreIds + " не существуют");
            }
        }

        Film film = FilmMapper.toFilm(dto, mpa);
        return FilmMapper.mapToFilmDto(filmRepository.create(film));
    }

    private void validateFilmDto(FilmDto dto) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

        if (dto.getReleaseDate() != null && dto.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата выпуска не может быть раньше " + minReleaseDate);
        }
        if (dto.getDuration() != null && dto.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    public FilmDto update(FilmDto dto) {
        validateFilmDto(dto);

        Long filmId = dto.getId();
        Film film = filmRepository.getById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        Mpa mpa = mpaRepository.getMpaById(dto.getMpa().getId())
                .orElseThrow(() -> new ValidationException(
                        "MPA с ID " + dto.getMpa().getId() + " не существует"));

        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            Set<Long> genreIds = dto.getGenres().stream()
                    .map(GenreDto::getId)
                    .collect(Collectors.toSet());
            List<Genre> existingGenres = genreRepository.getGenresByIds(genreIds);
            if (existingGenres.size() != genreIds.size()) {
                throw new NotFoundException("Один или несколько жанров с ID " + genreIds + " не существуют");
            }
        }

        Film updatedFilm = filmRepository.update(FilmMapper.toFilm(dto, mpa));
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public List<FilmDto> getAll() {
        log.info("Получение всех фильмов");
        return filmRepository.getAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(Long id) {
        log.info("Поиск фильма с ID: {}", id);
        Film film = filmRepository.getById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        Set<Genre> genres = loadGenresForFilm(id);
        film.setGenres(genres);

        return FilmMapper.mapToFilmDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);

        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        filmRepository.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) throws BadRequestException {
        log.info("Пользователь с ID {} убрал лайк у фильма с ID {}", userId, filmId);

        filmRepository.deleteLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        log.info("Получение топ-{} популярных фильмов", count);
        return filmRepository.getPopularFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private Set<Genre> loadGenresForFilm(Long filmId) {
        log.info("Загрузка жанров для фильма ID {} ", filmId);
        return filmRepository.loadGenresForFilm(filmId);
    }
}