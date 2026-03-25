package ru.yandex.practicum.filmorate.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
public class FilmResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    }
