package ru.yandex.practicum.filmorate.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value
@Data
@Builder
@AllArgsConstructor
public class UserRequest {

    Long id;

    @NotBlank(message = "Электронная почта не может быть пустой", groups = User.OnCreate.class)
    @Email(message = "Некорректный формат электронной почты", groups = {User.OnCreate.class, User.OnUpdate.class})
    String email;

    @NotBlank(message = "Логин не может быть пустым", groups = {User.OnCreate.class, User.OnUpdate.class})
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы", groups = {User.OnCreate.class, User.OnUpdate.class})
    String login;

    String name;

    @NotNull
    @Past(message = "Дата рождения не может быть в будущем", groups = {User.OnCreate.class, User.OnUpdate.class})
    LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}
