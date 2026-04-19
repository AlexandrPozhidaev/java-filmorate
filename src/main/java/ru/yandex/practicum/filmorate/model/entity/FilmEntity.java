package ru.yandex.practicum.filmorate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmEntity {

    private Long id;
    private String name;
    private String description;
    private Long duration;
    private LocalDate releaseDate;
    private Set<Long> likes;
    private Long mpaId;
    private List<Long> genreIds;
}
