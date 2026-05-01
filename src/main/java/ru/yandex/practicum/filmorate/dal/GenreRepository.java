package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class GenreRepository {

    private final JdbcTemplate jdbc;
    private GenreRowMapper genreRowMapper;

    public GenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public Set<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        List<Genre> genres = jdbc.query(sql, (rs, rowNum) ->
                new Genre(rs.getLong("id"), rs.getString("name"))
        );
        return new HashSet<>(genres);
    }

    public Optional<Genre> findById(Long id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try {
            Genre genre = jdbc.queryForObject(sql,
                    (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                    id);
            return Optional.ofNullable(genre);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Genre> findAllById(Set<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyList();

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM genres WHERE id IN (" + placeholders + ")";

        return jdbc.query(sql, new GenreRowMapper(), ids.toArray());
    }

}
