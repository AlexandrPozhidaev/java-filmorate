package ru.yandex.practicum.filmorate.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class FilmResponse {

    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private Set<Long> likes;
    private Long mpaId;
    private List<Long> genreIds;
    }
