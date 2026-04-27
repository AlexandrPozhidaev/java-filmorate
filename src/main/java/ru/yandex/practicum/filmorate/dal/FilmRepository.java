package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    public Film create(Film film) {
        String query = "INSERT INTO films (name, description, releaseDate, duration) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            if (film.getReleaseDate() != null) {
                ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setLong(4, film.getDuration());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);

        return film;
    }

    public Film update(Film film) {
        String query = "UPDATE films " +
                "SET name = ?, description = ?, releaseDate = ?, duration = ? " +
                "WHERE id = ?";

        int rowsAffected = jdbc.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        updateFilmGenres(film.getId(), film.getGenres());

        String selectSql = "SELECT * FROM films WHERE id = ?";
        return jdbc.queryForObject(selectSql, mapper, film.getId());
    }

    private void updateFilmGenres(Long filmId, Set<Long> genreIds) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        for (Long genreId : genreIds) {
            jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, genreId);
        }
    }

    public List<Film> getAll() {
        String sql = "SELECT f.*, f.mpa_id as mpa_id " +
                "FROM films f " +
                "ORDER BY f.id";
        return jdbc.query(sql, (rs, rowNum) -> mapRowWithMpa(rs));
    }


    public Optional<Film> getById(Long id) {
        String sql = "SELECT f.*, f.mpa_id as mpa_id " +
                "FROM films f " +
                "WHERE f.id = ?";
        try {
            Film film = jdbc.queryForObject(sql, (rs, rowNum) -> mapRowWithMpa(rs), id);
            film.setGenres(loadGenresForFilm(id));
            film.setLikes(loadLikesForFilm(id));
            return Optional.of(film);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Set<Long> loadGenresForFilm(Long filmId) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        List<Long> genres = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(genres);
    }

    private Set<Long> loadLikesForFilm(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likes = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(likes);
    }

    private Film mapRowWithMpa(ResultSet resultSet) throws SQLException {
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

        Long mpaId = resultSet.getLong("mpa_id");
        if (!resultSet.wasNull()) {
            film.setMpa(Set.of(mpaId));
        } else {
            film.setMpa(new HashSet<>());
        }

        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        return film;
    }


    public void addLike(Long filmId, Long userId) {
        String checkSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        int count = jdbc.queryForObject(checkSql, Integer.class, filmId, userId);

        if (count > 0) {
            throw new RuntimeException("Пользователь ID " + userId + " уже поставил лайк фильму ID " + filmId);
        }

        String insertSql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(insertSql, filmId, userId);
    }

        public void deleteLike(Long filmId, Long userId) {
            String filmCheckSql = "SELECT COUNT(*) FROM films WHERE id = ?";
            int filmCount = jdbc.queryForObject(filmCheckSql, Integer.class, filmId);

            if (filmCount == 0) {
                throw new NotFoundException("Фильм с ID " + filmId + " не найден");
            }

            String likeCheckSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
            int likeCount = jdbc.queryForObject(likeCheckSql, Integer.class, filmId, userId);

            if (likeCount == 0) {
                throw new NotFoundException(
                        "Пользователь с ID " + userId + " не ставил лайк фильму с ID " + filmId
                );
            }

            String deleteSql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
            jdbc.update(deleteSql, filmId, userId);
        }


    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, COUNT(fl.user_id) as likes_count " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id " +
                "ORDER BY likes_count DESC, f.id " +
                "LIMIT ?";

        List<Film> films = jdbc.query(sql, mapper, count);

        for (Film film : films) {
            film.setGenres(loadGenresForFilm(film.getId()));
            film.setLikes(loadLikesForFilm(film.getId()));
        }
        return films;
    }
}