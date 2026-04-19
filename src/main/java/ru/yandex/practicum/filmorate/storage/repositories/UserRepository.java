package ru.yandex.practicum.filmorate.storage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.model.entity.UserEntity;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u " +
            "JOIN FETCH u.friends f " +
            "WHERE u.id = :userId")
    UserEntity findByIdWithFriends(Long userId);

    @Query("SELECT DISTINCT u1 FROM UserEntity u1 " +
            "JOIN u1.friends f1 " +
            "JOIN u2.friends f2 " +
            "WHERE u1.id = f2.id AND u2.id = :userId1 AND f1.id = :userId2")
    List<UserEntity> findCommonFriends(Long userId1, Long userId2);
}
