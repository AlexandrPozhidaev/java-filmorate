package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Long, Film> films = new HashMap<>();
    private Long generateId = 0L;

    @Override
    public Film create(Film film) {
        film.setId(++generateId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            return false;
        }

        Set<Long> likes = film.getLikes();
        if (likes.contains(userId)) {
            return false;
        }

        likes.add(userId);
        return true;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            return false;
        }

        Set<Long> likes = film.getLikes();
        if (!likes.contains(userId)) {
            return false;
        }

        likes.remove(userId);
        return true;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        int actualCount = count <= 0 ? 10 : count;

        return films.values().stream()
                .sorted((f1, f2) -> {
                    int likesComparison = Integer.compare(
                            f2.getLikes().size(),
                            f1.getLikes().size()
                    );
                    if (likesComparison != 0) {
                        return likesComparison;
                    }

                    return f2.getReleaseDate().compareTo(f1.getReleaseDate());
                })
                .limit(actualCount)
                .collect(Collectors.toList());
    }

    @Override
    public Film getById(Long id) {
          return films.get(id);
    }

}
