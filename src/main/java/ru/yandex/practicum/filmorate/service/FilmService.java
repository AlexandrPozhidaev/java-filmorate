package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
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
    private final UserRepository userRepository;


    public FilmDto create(FilmDto dto) {
        validateFilmDto(dto);
        Film film = FilmMapper.toFilm(dto);
        log.info("Создание фильма: {}", film);
        Film createdFilm = filmRepository.create(film);
        return FilmMapper.mapToFilmDto(createdFilm);
    }

    private void validateFilmDto(FilmDto dto) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

        if (dto.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата выпуска не может быть раньше 28.12.1895");
        }
    }

    public FilmDto update(FilmDto dto) {
        validateFilmDto(dto);

        Film film = FilmMapper.toFilm(dto);
        log.info("Обновление фильма с ID: {}", film.getId());

        Film updatedFilm = filmRepository.update(film);
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
        Film film = filmRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
        return FilmMapper.mapToFilmDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);

        // Проверка существования пользователя
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


}
