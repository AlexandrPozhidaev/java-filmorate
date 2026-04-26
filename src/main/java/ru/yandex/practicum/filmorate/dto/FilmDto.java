package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(min = 1, max = 100, message = "Название должно быть от 1 до 100 символов")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    @NotNull(message = "Описание не может быть null")
    private String description;

    @NotNull(message = "Дата выпуска обязательна")
    @PastOrPresent(message = "Дата выпуска не может быть в будущем")
    @FutureOrPresent(message = "Дата выпуска не может быть слишком далёким прошлым")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;

    @NotNull(message = "Рейтинг MPAA обязателен")
    private MpaDto mpa;

    @NotEmpty(message = "Фильм должен иметь хотя бы один жанр")
    @Size(max = 5, message = "Фильм не может иметь более 5 жанров")
    private List<GenreDto> genre;
}

