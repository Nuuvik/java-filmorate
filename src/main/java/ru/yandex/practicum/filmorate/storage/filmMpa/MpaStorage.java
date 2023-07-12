package ru.yandex.practicum.filmorate.storage.filmMpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    Mpa getMpaById(int id);

    List<Mpa> getAllMpa();
}
