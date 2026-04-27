package ru.yandex.practicum.filmorate.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    }
