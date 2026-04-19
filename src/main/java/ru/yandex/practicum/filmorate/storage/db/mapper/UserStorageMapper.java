package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.entity.UserEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserStorageMapper {

    User toModel(UserEntity userEntity);

    UserEntity toEntity(User user);
}
