package ru.yandex.practicum.filmorate.controller.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collections;

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
        dto.setMpa(film.getMpa());
        if (film.getGenres() != null) {
            dto.setGenres(GenreMapper.toDto(film.getGenres()));
        } else {
            dto.setGenres(Collections.emptySet());
        }
        dto.setLikes(film.getLikes());

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
        film.setMpa(dto.getMpa());
        film.setGenres(GenreMapper.toGenres(dto.getGenres()));
        film.setLikes(dto.getLikes());

        return film;
    }

    public static Film toFilm(FilmDto dto, Mpa mpa) {
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
        film.setMpa(mpa); // устанавливаем MPA из параметра метода
        film.setGenres(GenreMapper.toGenres(dto.getGenres()));
        film.setLikes(dto.getLikes());

        return film;
    }
}