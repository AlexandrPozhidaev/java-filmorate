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
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final GenreRepository genreRepository;

    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
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
        jdbc.update("DELETE FROM film_likes WHERE film_id = ?", filmId);
        for (Long userId : userIds) {
            jdbc.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        }
    }

    public Film update(Film film) {
        String query = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ? " +
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

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet())));


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
        return jdbc.query(sql, mapper); // используем готовый mapper
    }


    public Optional<Film> getById(Long id) {
        String sql = "SELECT f.*, f.mpa_id as mpa_id " +
                "FROM films f " +
                "WHERE f.id = ?";

            return Optional.empty();

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

        return films;
    }
}