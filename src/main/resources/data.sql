INSERT INTO MPA (id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG_13'),
(4, 'R'),
(5, 'NC_17');

INSERT INTO GENRES (id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO USERS (id, email, login, name, birthday) VALUES
(1, 'testUser1@test.ru', 'testUser1', 'Test User 1', '1993-02-11'),
(2, 'testUser2@test.ru', 'testUser2', 'Test User 2', '1993-02-25'),
(3, 'testUser3@test.ru', 'testUser3', 'Test User 3', '2000-10-05');

INSERT INTO FILMS(id, name, description, duration, release_date, rate, mpa_id) VALUES
(1, 'TestFilm1', 'Description1', 120, '2021-01-01', 0, 1),
(2, 'TestFilm2', 'Description2', 90, '2022-02-02', 0, 2);