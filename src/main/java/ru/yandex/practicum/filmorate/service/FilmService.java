package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;

@Service
public class FilmService {

    private static final String NOT_FOUND_FILM = "фильма с id %s нет";
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeService likeService;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, UserService userService, LikeService likeService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.likeService = likeService;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        Film filmFromBD = filmStorage.getFilmById(film.getId());

        checkFilmIsNotFound(filmFromBD, film.getId());

        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        likeService.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);

        likeService.deleteLikeFromFilm(filmId, user.getId());
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);

        checkFilmIsNotFound(film, id);

        return film;
    }

    private void checkFilmIsNotFound(Film film, Integer id) {
        if (FilmValidator.isFilmNull(film)) {
            throw new UpdateException(String.format(NOT_FOUND_FILM, id));
        }
    }
}
