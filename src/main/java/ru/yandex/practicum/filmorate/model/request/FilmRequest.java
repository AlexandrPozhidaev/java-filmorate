package ru.yandex.practicum.filmorate.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class FilmRequest {

    Long id;

    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    String description;

    @NotNull
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Long duration;

    private Set<Long> likes = new HashSet<>();
}