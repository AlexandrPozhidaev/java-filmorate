CREATE TABLE IF NOT EXISTS USERS (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE,
    CONSTRAINT unique_email UNIQUE (email),
    CONSTRAINT unique_login UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS MPA (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    duration INT NOT NULL,
    release_date DATE NOT NULL,
    rate INT DEFAULT 0,
    mpa_id INT,
    CONSTRAINT fk_films_mpa FOREIGN KEY (mpa_id) REFERENCES MPA(id)
);

CREATE TABLE IF NOT EXISTS GENRES (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS LIKES (
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    PRIMARY KEY (user_id, film_id),
    CONSTRAINT fk_likes_users FOREIGN KEY (user_id) REFERENCES USERS(id),
    CONSTRAINT fk_likes_films FOREIGN KEY (film_id) REFERENCES FILMS(id)
);

CREATE TABLE IF NOT EXISTS FRIENDS (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friends_user FOREIGN KEY (user_id) REFERENCES USERS(id),
    CONSTRAINT fk_friends_friend FOREIGN KEY (friend_id) REFERENCES USERS(id)
);

CREATE TABLE IF NOT EXISTS FILM_GENRES (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genres_films FOREIGN KEY (film_id) REFERENCES FILMS(id),
    CONSTRAINT fk_film_genres_genres FOREIGN KEY (genre_id) REFERENCES GENRES(id)
);
