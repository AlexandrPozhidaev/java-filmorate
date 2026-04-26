package ru.yandex.practicum.filmorate.controller.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        // Преобразуем MPA
        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(film.getMpaId());
        dto.setMpa(mpaDto);

        // Преобразуем жанры
        List<GenreDto> genreDtos = film.getGenreIds().stream()
                .map(genreId -> {
                    GenreDto genreDto = new GenreDto();
                    genreDto.setId(genreId);
                    return genreDto;
                })
                .collect(Collectors.toList());
        dto.setGenre(genreDtos);

        return dto;
    }

    public static Film toFilm(FilmDto dto) {
        Film film = new Film();
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        film.setMpaId(dto.getMpa().getId());

        film.setGenreIds(dto.getGenre().stream()
                .map(GenreDto::getId)
                .collect(Collectors.toSet()));
        return film;
    }
}
