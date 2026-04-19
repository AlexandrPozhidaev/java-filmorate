package ru.yandex.practicum.filmorate.storage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.filmorate.model.entity.FilmEntity;

import java.util.List;

public interface FilmRepository extends JpaRepository<FilmEntity, Long> {
    @Query("SELECT f FROM FilmEntity f " +
            "LEFT JOIN FETCH f.likes l " +
            "GROUP BY f " +
            "ORDER BY COUNT(l) DESC " +
            "LIMIT :count")
    List<FilmEntity> findPopularFilms(@Param("count") int count);
}
