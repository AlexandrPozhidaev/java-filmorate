-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    login VARCHAR(255) UNIQUE NOT NULL,
    birthday DATE
);

-- Таблица рейтингов MPA (Movie Picture Association)
CREATE TABLE IF NOT EXISTS mpa (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL CHECK (duration > 0),
    mpa_id BIGINT,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id) ON DELETE SET NULL
);

-- Таблица жанров
CREATE TABLE IF NOT EXISTS genres (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255)
);

-- Связующая таблица для связи фильмов и жанров (многие‑ко‑многим)
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT REFERENCES films (id),
    genre_id INT REFERENCES genres(id),
    PRIMARY KEY (film_id, genre_id)
);

-- Таблица дружбы между пользователями
CREATE TABLE IF NOT EXISTS friends (
  user_id INT NOT NULL REFERENCES users(id),
  friend_id INT NOT NULL REFERENCES users(id),  -- исправлено: убрано IS
  PRIMARY KEY (user_id, friend_id)
);

-- Таблица лайков фильмов (связь пользователей и фильмов)
CREATE TABLE IF NOT EXISTS likes (
    user_id INT NOT NULL REFERENCES users(id),
    film_id INT NOT NULL REFERENCES films(id),
    PRIMARY KEY (user_id, film_id)
);

-- Рейтинги MPA
MERGE INTO mpa (id, name) VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

-- Жанры
MERGE INTO genres (id, name) VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');
