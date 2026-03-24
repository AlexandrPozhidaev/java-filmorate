package ru.yandex.practicum.filmorate.storage;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;
    private Long generateId = 0L;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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

        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        film.getLikes().add(userId);
        update(film);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) throws BadRequestException {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        if (!film.getLikes().contains(userId)) {
            throw new BadRequestException("Пользователь с ID " + userId + " не ставил лайк этому фильму");
        }

        film.getLikes().remove(userId);
        update(film); // используем локальный update, а не filmStorage.update
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
