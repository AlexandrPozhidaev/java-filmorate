package ru.yandex.practicum.filmorate.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "friends", ignore = true)
    User toUser(UserRequest request);

    UserResponse toResponse(User user);
}
