package ru.yandex.practicum.filmorate.storage.filmMpa;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface FilmMpaStorage {

    void addFilmMpa(Integer filmId, Integer mpaId);

    Mpa getFilmMpaById(Integer filmId);

    void deleteFilmMpaById(Integer filmId);
}
