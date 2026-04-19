package ru.yandex.practicum.filmorate.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;
import ru.yandex.practicum.filmorate.model.response.FilmResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FilmMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "mpaId", ignore = true)
    @Mapping(target = "genreIds", ignore = true)
    Film toFilm(FilmRequest filmRequest);

    @Mapping(source = "likes", target = "likes")
    @Mapping(source = "mpaId", target = "mpaId")
    @Mapping(source = "genreIds", target = "genreIds")
    FilmResponse toResponse(Film film);

    default List<Long> setToGenreIds(Set<Long> genreIds) {
        return genreIds != null ? new ArrayList<>(genreIds) : new ArrayList<>();
    }

    default Set<Long> listToGenreIds(List<Long> genreIds) {
        return genreIds != null ? new HashSet<>(genreIds) : new HashSet<>();
    }
}