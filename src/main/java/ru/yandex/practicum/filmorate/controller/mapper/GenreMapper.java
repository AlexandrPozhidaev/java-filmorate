package ru.yandex.practicum.filmorate.controller.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreMapper {
    public GenreDto toDto(Genre genre) {
        if (genre == null) return null;
        return new GenreDto();
    }

    public List<GenreDto> toDtoList(List<Genre> genreList) {
        return genreList.stream()
                .map(this::toDto)
                .toList();
    }
}
