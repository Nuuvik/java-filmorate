package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmMpa.MpaDbStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public Mpa getMpaById(int id) {
        try {
            return mpaDbStorage.getMpaById(id);
        } catch (Exception e) {
            throw new NotFoundException("Мпа не найден");
        }
    }

    public List<Mpa> getAllMpa() {
        try {
            return mpaDbStorage.getAllMpa()
                    .stream()
                    .sorted((o1, o2) -> o1.getId() - o2.getId())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new NotFoundException("Мпа не найден");
        }
    }
}
