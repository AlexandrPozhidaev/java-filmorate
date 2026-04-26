package ru.yandex.practicum.filmorate.controller.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Component
public class MpaMapper {
    public MpaDto toDto(Mpa mpa) {
        if (mpa == null) return null;
        return new MpaDto();
    }

    public List<MpaDto> toDtoList(List<Mpa> mpaList) {
        return mpaList.stream()
                .map(this::toDto)
                .toList();
    }
}
