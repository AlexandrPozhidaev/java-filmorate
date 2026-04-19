package ru.yandex.practicum.filmorate.model.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private Long id;
    private String name;
    @NotBlank(message = "Электронная почта не может быть пустой", groups = User.OnCreate.class)
    @Email(message = "Некорректный формат электронной почты", groups = {User.OnCreate.class, User.OnUpdate.class})
    private String email;
    @NotBlank(message = "Логин не может быть пустым", groups = {User.OnCreate.class, User.OnUpdate.class})
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы", groups = {User.OnCreate.class, User.OnUpdate.class})
    private String login;
    @NotNull
    @Past(message = "Дата рождения не может быть в будущем", groups = {User.OnCreate.class, User.OnUpdate.class})
    private LocalDate birthday;
    @Builder.Default
    private Set<Long> friends = new HashSet<>();
}
