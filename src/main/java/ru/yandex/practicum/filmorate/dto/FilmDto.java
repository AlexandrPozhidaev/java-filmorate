package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class FilmDto {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 100, message = "Название должно быть от 1 до 100 символов")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @PastOrPresent(message = "Дата выпуска не может быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Дата выпуска обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;

    private Set<Long> likes = new HashSet<>();

    private Set<Long> mpa = new HashSet<>();

    private Set<Long> genres = new HashSet<>();
}

