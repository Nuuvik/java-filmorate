package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film createFilm(Film film);

    Film getFilmById(Integer id);

    Collection<Film> getAllFilms();

    Film updateFilm(Film film);

    Collection<Film> getPopularFilms(Integer count);
}
