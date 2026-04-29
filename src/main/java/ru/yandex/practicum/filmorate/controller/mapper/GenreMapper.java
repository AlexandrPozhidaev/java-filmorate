package ru.yandex.practicum.filmorate.controller.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreMapper {

    // Genre -> GenreDto (для одного объекта)
    public static GenreDto toDto(Genre genre) {
        if (genre == null) return null;
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Set<GenreDto> toDto(Set<Genre> genres) {
        return genres.stream()
                .map(GenreMapper::toDto)  // Используем метод для одного объекта
                .collect(Collectors.toSet());
    }

    // Set<Genre> -> Set<GenreDto>
    public static Set<GenreDto> toGenreDto(Set<Genre> genres) {
        return genres.stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toSet());
    }

    // Set<GenreDto> -> Set<Genre>
    public static Set<Genre> toGenres(Set<GenreDto> genreDtos) {
        return genreDtos.stream()
                .map(dto -> {
                    Genre genre = new Genre();
                    genre.setId(dto.getId());
                    genre.setName(dto.getName());
                    return genre;
                })
                .collect(Collectors.toSet());
    }
}
