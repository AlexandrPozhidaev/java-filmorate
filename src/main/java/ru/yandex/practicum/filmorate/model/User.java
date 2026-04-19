package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

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

    @NotBlank(message = "Электронная почта не может быть пустой", groups = OnCreate.class)
    @Email(message = "Некорректный формат электронной почты", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы", groups = {OnCreate.class, OnUpdate.class})
    private String login;

    private String name;

    @NotNull
    @Past(message = "Дата рождения не может быть в будущем", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}