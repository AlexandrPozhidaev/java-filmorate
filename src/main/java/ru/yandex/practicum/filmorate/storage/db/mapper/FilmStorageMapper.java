package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.entity.FilmEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FilmStorageMapper {

    @Mapping(source = "likes", target = "likes")
    @Mapping(source = "mpaId", target = "mpaId")
    @Mapping(source = "genreIds", target = "genreIds")
    Film toModel(FilmEntity filmEntity);

    @Mapping(source = "likes", target = "likes")
    @Mapping(source = "mpaId", target = "mpaId")
    @Mapping(source = "genreIds", target = "genreIds")
    FilmEntity toEntity(Film film);
}
