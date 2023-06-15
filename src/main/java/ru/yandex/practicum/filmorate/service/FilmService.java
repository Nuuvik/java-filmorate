package ru.yandex.practicum.filmorate.service;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private static final String NOT_FOUND_FILM = "фильма с id %s нет";
    private static final String NOT_FOUND_USER = "пользователя с userId %s нет";
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.filmStorage = inMemoryFilmStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        checkFilmIsNotNull(film, film.getId());

        return filmStorage.update(film);
    }

    public Map<Integer, Film> getFilms() {
        return filmStorage.getAll();
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);

        checkFilmIsNotNull(film, film.getId());

        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);

        checkFilmIsNotNull(film, film.getId());
        checkUserIdIsValid(userId);

        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage
                .getAll()
                .values()
                .stream()
                .sorted((Film film1, Film film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getById(id);

        checkFilmIsNotNull(film, id);

        return filmStorage.getById(id);
    }

    private void checkFilmIsNotNull(Film film, Integer id) {
        if (FilmValidator.isFilmNotFound(getFilms(), film)) {
            throw new UpdateException(String.format(NOT_FOUND_FILM, id));
        }
    }

    private void checkUserIdIsValid(Integer userId) {
        if (userId <= 0) {
            throw new UpdateException(String.format(NOT_FOUND_USER, userId));
        }
    }
}