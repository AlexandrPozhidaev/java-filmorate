package ru.yandex.practicum.filmorate.storage.db.memory;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
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
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        Set<Long> updatedLikes = new HashSet<>(film.getLikes());
        updatedLikes.add(userId);
        Film updatedFilm = Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(updatedLikes)
                .build();
        films.put(filmId, updatedFilm);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        Set<Long> updatedLikes = new HashSet<>(film.getLikes());
        updatedLikes.remove(userId);
        Film updatedFilm = Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(updatedLikes)
                .build();
        films.put(filmId, updatedFilm);
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
    public Optional<Film> getById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

}
