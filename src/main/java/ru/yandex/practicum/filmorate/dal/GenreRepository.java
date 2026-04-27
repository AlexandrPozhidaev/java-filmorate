package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public GenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getLong("id"), rs.getString("name"))
        );
    }

    public Optional<Genre> findById(Long id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                    id);
            return Optional.ofNullable(genre);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Genre> findAllById(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        String placeholders = genreIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String sql = "SELECT id, name FROM genres WHERE id IN (" + placeholders + ") ORDER BY id";

        List<Object> parameters = new ArrayList<>(genreIds);

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                parameters.toArray()
        );
    }
}
