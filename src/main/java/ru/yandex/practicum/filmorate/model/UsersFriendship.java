package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersFriendship {
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;
    private LocalDate createdAt;
}
