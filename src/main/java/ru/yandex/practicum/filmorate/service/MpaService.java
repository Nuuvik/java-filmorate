package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.validator.MpaValidator;

import java.util.Collection;

@Service
public class MpaService {

    private static final String NOT_FOUND_MESSAGE = "MPA рейтинга с id %s нет";
    private final MpaStorage mpaStorage;

    public MpaService(@Qualifier("MpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMapById(Integer id) {
        Mpa mpa = mpaStorage.getMpaById(id);

        checkMpaIsNotNull(mpa, id);

        return mpa;
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    private void checkMpaIsNotNull(Mpa mpa, Integer id) {
        if (MpaValidator.isMpaNotFound(getAllMpa(), mpa)) {
            throw new UpdateException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }
}
