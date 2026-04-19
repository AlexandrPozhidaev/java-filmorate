package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.entity.MpaEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MpaStorageMapper {

    MpaEntity toEntity(Mpa mpa);

    Mpa toModel(MpaEntity rating);
}
