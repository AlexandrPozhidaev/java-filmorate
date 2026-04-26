package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class GenreDto {
    private Long id;
    private String name;
}