package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    public interface OnCreate {
    }

    public interface OnUpdate {
    }

    private Long id;

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;

    @Builder.Default
    private Set<User> friends = new HashSet<>();
}