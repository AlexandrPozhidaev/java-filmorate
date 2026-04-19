# java-filmorate
Template repository for Filmorate project.

## Визуализация диаграммы

src/database/database_diagramma.png

## Примеры запросов 

- Вывод пользователей:

SELECT id, email, login, name, birthday
FROM users
ORDER BY login;

- Вывод фильмов

SELECT f.id, f.name, f.description, f.release_date, f.duration, mr.name AS mpa_rating
FROM films f
JOIN mpa_ratings mr ON f.mpa_rating_id = mr.id
ORDER BY f.name;

  - Вывод друзей пользователя

SELECT u.id, u.login, u.name
FROM users u
JOIN friendships f ON u.id = f.friend_id
WHERE f.user_id = 1 AND f.status = 'CONFIRMED';