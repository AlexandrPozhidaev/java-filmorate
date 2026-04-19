package ru.yandex.practicum.filmorate.model.request;

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
    private String name;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private Long mpaId;
    private Set<Long> genreIds = new HashSet<>();
}