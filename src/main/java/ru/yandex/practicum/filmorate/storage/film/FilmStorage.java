package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {

    Film create(Film film);

    Film getById(Integer id);

    Map<Integer, Film> getAll();

    Film update(Film film);

    void deleteById(Integer id);
}