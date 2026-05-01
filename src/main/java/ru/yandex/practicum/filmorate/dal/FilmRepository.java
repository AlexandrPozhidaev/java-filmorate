package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;

    @Autowired
    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper filmRowMapper, GenreRowMapper genreRowMapper) {
        this.jdbc = jdbc;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
    }

    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, RELEASE_DATE, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());

            if (film.getMpa() != null) {
                stmt.setLong(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            return stmt;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);

        // Сохраняем likes отдельно
        updateFilmLikes(generatedId, film.getLikes());

        return film;
    }

    private void updateFilmLikes(Long filmId, Set<Long> userIds) {
        jdbc.update("DELETE FROM likes WHERE film_id = ?", filmId);
        for (Long userId : userIds) {
            jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        }
    }

    public Film update(Film film) {
        String updateFilmQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";

        int rowsAffected = jdbc.update(updateFilmQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new EntityNotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        updateFilmGenres(film.getId(), film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet()));

        String selectSql = "SELECT f.*, f.mpa_id as mpa_id " +
                "FROM films f " +
                "WHERE f.id = ?";
        return jdbc.queryForObject(selectSql, filmRowMapper, film.getId());
    }

    private void updateFilmGenres(Long filmId, Set<Long> genreIds) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        for (Long genreId : genreIds) {
            jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, genreId);
        }
    }

    public List<Film> getAll() {
        String sql = "SELECT f.*, f.mpa_id AS mpa_id "
                + "FROM films f "
                + "ORDER BY f.id";
        return jdbc.query(sql, filmRowMapper); // используем готовый mapper
    }


    public Film getById(Long id) {
        String sql = "SELECT f.*, f.mpa_id as mpa_id FROM films f WHERE f.id = ?";
        return jdbc.queryForObject(sql, filmRowMapper, id);
    }

    private Set<Long> loadGenresForFilm(Long filmId) {
        String sql = "SELECT genre_id FROM genres WHERE film_id = ?";
        List<Long> genres = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(genres);
    }

    private Set<Long> loadLikesForFilm(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbc.queryForList(sql, Long.class, filmId);
        return new HashSet<>(likes);
    }

    public void addLike(Long filmId, Long userId) {
        String checkSql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        int count = jdbc.queryForObject(checkSql, Integer.class, filmId, userId);

        if (count > 0) {
            throw new RuntimeException("Пользователь ID " + userId + " уже поставил лайк фильму ID " + filmId);
        }

        String insertSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(insertSql, filmId, userId);
    }

        public void deleteLike(Long filmId, Long userId) {
            String filmCheckSql = "SELECT COUNT(*) FROM films WHERE id = ?";
            int filmCount = jdbc.queryForObject(filmCheckSql, Integer.class, filmId);

            if (filmCount == 0) {
                throw new NotFoundException("Фильм с ID " + filmId + " не найден");
            }

            String likeCheckSql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
            int likeCount = jdbc.queryForObject(likeCheckSql, Integer.class, filmId, userId);

            if (likeCount == 0) {
                throw new NotFoundException(
                        "Пользователь с ID " + userId + " не ставил лайк фильму с ID " + filmId
                );
            }

            String deleteSql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
            jdbc.update(deleteSql, filmId, userId);
        }


    public List<Film> getPopularFilms(int count) {
        String sql = """
            SELECT f.*, g.id as genre_id, g.name as genre_name,
                   m.id as mpa_id, m.name as mpa_name,
                   l.user_id as like_user_id
            FROM films f
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            LEFT JOIN mpa m ON f.mpa_id = m.id
            LEFT JOIN likes l ON f.id = l.film_id
            ORDER BY (SELECT COUNT(*) FROM likes WHERE film_id = f.id) DESC
            LIMIT ?
            """;

        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbc.query(sql, (rs, rowNum) -> {
            Long filmId = rs.getLong("id");
            Film film = filmMap.computeIfAbsent(filmId, id -> {
                try {
                    return filmRowMapper.mapRow(rs, rowNum);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            // Добавляем жанр, если он есть
            if (rs.getObject("genre_id") != null) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }

            // Добавляем лайк, если он есть
            if (rs.getObject("like_user_id") != null) {
                film.getLikes().add(rs.getLong("like_user_id"));
            }

            return film;
        }, count);

        return new ArrayList<>(filmMap.values());
    }
}