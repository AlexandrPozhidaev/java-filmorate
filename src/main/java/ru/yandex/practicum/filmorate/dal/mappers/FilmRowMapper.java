package ru.yandex.practicum.filmorate.dal.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DataAccessException;
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

    private static final Logger log = LoggerFactory.getLogger(FilmRowMapper.class);

    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;

    @Autowired
    public FilmRowMapper(JdbcTemplate jdbc, GenreRowMapper genreRowMapper) {
        this.jdbc = jdbc;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        log.debug("Начинаем маппинг фильма, строка №{}", rowNum);
        Film film = new Film();
        film.setId(resultSet.getLong("id"));

        String name = resultSet.getString("name");
        if (name == null || name.trim().isEmpty()) {
            throw new DataAccessException("Поле 'name' не может быть пустым для фильма ID=" + film.getId());
        }
        film.setName(name);

        film.setDescription(resultSet.getString("description"));

        java.sql.Date sqlDate = resultSet.getDate("release_date");
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

        // Инициализируем пустые коллекции (данные загружаем отдельно)
        film.setGenres(Set.of());
        film.setLikes(Set.of());

        log.debug("Завершили маппинг фильма ID={}", film.getId());
        return film;
    }

    private List<Genre> loadGenresForFilm(Long filmId) {
        if (filmId == null) {
            return List.of();
        }
        String sql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        return jdbc.query(sql, genreRowMapper, filmId);
    }

    private Set<Long> loadLikesForFilm(Long filmId) {
        try {
            if (filmId == null) {
                return Set.of();
            }
            String sql = "SELECT user_id FROM likes WHERE film_id = ?";
            List<Long> likes = jdbc.queryForList(sql, Long.class, filmId);
            return new HashSet<>(likes);
        } catch (Exception e) {
            log.warn("Не удалось загрузить лайки для фильма ID={}", filmId, e);
            return Set.of();
        }
    }
}
