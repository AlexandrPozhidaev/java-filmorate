package ru.yandex.practicum.filmorate.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class FilmRequest {

    @NotNull(groups = User.OnUpdate.class)
    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    String description;

    @NotNull(message = "Дата выпуска обязательна")
    @PastOrPresent(message = "Дата выпуска не может быть в будущем")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Long duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();
}