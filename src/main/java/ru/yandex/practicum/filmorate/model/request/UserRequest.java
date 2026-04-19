package ru.yandex.practicum.filmorate.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Data
@Builder
@AllArgsConstructor
public class UserRequest {

    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой", groups = User.OnCreate.class)
    @Email(message = "Некорректный формат электронной почты", groups = {User.OnCreate.class, User.OnUpdate.class})
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = {User.OnCreate.class, User.OnUpdate.class})
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы", groups = {User.OnCreate.class, User.OnUpdate.class})
    private String login;

    private String name;

    @NotNull
    @Past(message = "Дата рождения не может быть в будущем", groups = {User.OnCreate.class, User.OnUpdate.class})
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}
