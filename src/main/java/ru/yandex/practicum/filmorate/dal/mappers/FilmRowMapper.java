package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    private JdbcTemplate jdbc;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        java.sql.Date sqlDate = resultSet.getDate("releaseDate");
        if (sqlDate != null) {
            film.setReleaseDate(sqlDate.toLocalDate());
        } else {
            film.setReleaseDate(null);
        }

        long duration = resultSet.getLong("duration");
        if (resultSet.wasNull()) {
            film.setDuration(0L);
        } else {
            film.setDuration(duration);
        }

        // Загружаем MPA
        Long mpaId = resultSet.getLong("mpa_id");
        if (!resultSet.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            film.setMpa(mpa);
        }

        // Загружаем жанры и лайки
        Set<Genre> genres = loadGenresForFilm(resultSet.getLong("id"));
        film.setGenres(genres);
        film.setLikes(loadLikesForFilm(resultSet.getLong("id")));

        return film;
    }

    private Set<Genre> loadGenresForFilm(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbc.query(sql,
                (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                filmId);
        return new HashSet<>(genres);
    }

    private Set<Long> loadLikesForFilm(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likes = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(likes);
    }
}
