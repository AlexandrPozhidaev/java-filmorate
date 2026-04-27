package ru.yandex.practicum.filmorate.controller.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        if (film == null) {
            return null;
        }

        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(new HashSet<>(film.getMpa()));
        } else {
            dto.setMpa(new HashSet<>());
        }

        if (film.getGenres() != null) {
            dto.setGenres(new HashSet<>(film.getGenres()));
        } else {
            dto.setGenres(new HashSet<>());
        }

        return dto;
    }

    public static Film toFilm(FilmDto dto) {
        if (dto == null) {
            return null;
        }

        Film film = new Film();

        if (dto.getId() != null) {
            film.setId(dto.getId());
        }
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        // Копируем MPA ID
        if (dto.getMpa() != null) {
            film.setMpa(new HashSet<>(dto.getMpa()));
        } else {
            film.setMpa(new HashSet<>());
        }

        // Копируем жанры ID
        if (dto.getGenres() != null) {
            film.setGenres(new HashSet<>(dto.getGenres()));
        } else {
            film.setGenres(new HashSet<>());
        }

        film.setLikes(new HashSet<>()); // likes не копируем из DTO

        return film;
    }

    public static void updateFilmFromDto(FilmDto dto, Film film) {
        if (dto == null || film == null) {
            return;
        }

        if (dto.getName() != null) {
            film.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            film.setDescription(dto.getDescription());
        }
        if (dto.getReleaseDate() != null) {
            film.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getDuration() != null) {
            film.setDuration(dto.getDuration());
        }
        if (dto.getMpa() != null) {
            film.setMpa(new HashSet<>(dto.getMpa()));
        }
        if (dto.getGenres() != null) {
            film.setGenres(new HashSet<>(dto.getGenres()));
        }
    }
}