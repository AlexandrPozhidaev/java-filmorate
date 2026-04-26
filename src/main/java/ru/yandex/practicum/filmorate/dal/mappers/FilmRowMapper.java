package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {
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
            film.setDuration(0L); // значение по умолчанию
        } else {
            film.setDuration(duration);
        }

        Long mpaId = resultSet.getLong("mpa_id");
        if (resultSet.wasNull()) {
            film.setMpaId(null);
        } else {
            film.setMpaId(mpaId);
        }

        // Заполняем жанры и лайки через отдельные запросы
        film.setLikes(new HashSet<>());
        film.setGenreIds(new HashSet<>());

        return film;
    }
}
