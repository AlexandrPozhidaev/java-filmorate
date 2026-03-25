package ru.yandex.practicum.filmorate.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class FilmRequest {

    @NotNull(groups = User.OnUpdate.class)
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NotNull(message = "Дата выпуска обязательна")
    @ValidReleaseDate(message = "Дата релиза не может быть раньше 28/12/1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();
}