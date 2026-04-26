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

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    public Film create(Film film) {
        String query = "INSERT INTO films (name, description, releaseDate, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setLong(4, film.getDuration());
            ps.setLong(5, film.getMpaId());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);

        updateFilmGenres(generatedId, film.getGenreIds());

        return film;
    }

    public Film update(Film film) {
        String query = "UPDATE films " +
                "SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        int rowsAffected = jdbc.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        updateFilmGenres(film.getId(), film.getGenreIds());

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
        String sql = "SELECT * FROM films ORDER BY id";
        return jdbc.query(sql, mapper);
    }

    public Optional<Film> getById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbc.queryForObject(sql, mapper, id);
            return Optional.of(film);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addLike(Long filmId, Long userId) {
        // Проверяем, не поставил ли уже пользователь лайк
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

        return jdbc.query(sql, mapper, count);
    }
}