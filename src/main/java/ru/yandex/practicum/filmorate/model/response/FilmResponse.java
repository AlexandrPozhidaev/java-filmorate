package ru.yandex.practicum.filmorate.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
public class FilmResponse {

    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Long duration;
}
