package ru.yandex.practicum.filmorate.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class UserResponse {

    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends;
}
