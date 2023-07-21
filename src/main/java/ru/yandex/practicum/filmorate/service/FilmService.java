package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.GenreStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public List<Film> getFilms() {
        return genreStorage.setGenresInFilm(filmStorage.getFilms());
    }

    public Film addFilm(Film film) {
        validate(film);
        return genreStorage.setGenreInFilm(filmStorage.addFilm(film));
    }

    public Film updateFilm(Film film) {
        validate(film);
        if (filmStorage.checkFilmExistInBd(film.getId())) {
            genreStorage.deleteFilmGenre(film);
            if (film.getDirectors().isEmpty()) {
                directorStorage.deleteDirectorsFromFilm(film.getId());
            }
            return genreStorage.setGenreInFilm(filmStorage.updateFilm(film));
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public Film getFilmById(int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        return genreStorage.setGenreInFilm(film);
    }

    public String deleteFilm(Integer id) {
        if (filmStorage.checkFilmExistInBd(id)) {
            filmStorage.deleteFilm(id);
            return String.format("Фильм с id %s удален", id);
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public Film addLike(int filmId, int userId) {
        if (filmStorage.checkFilmExistInBd(filmId)) {
            filmStorage.addLike(userId, filmId);
            log.info("Добавлен лайк от - {} фильму - {}", userId, filmId);
            return genreStorage.setGenreInFilm(filmStorage.getFilmById(filmId));
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public Film deleteLike(int filmId, int userId) {
        if (checkFilmAndLikeInExistInDb(filmId, userId)) {
            filmStorage.deleteLike(userId, filmId);
            log.info("Удален лайк - {} у фильму - {}", userId, filmId);
            return genreStorage.setGenreInFilm(filmStorage.getFilmById(filmId));
        } else if (!filmStorage.checkFilmExistInBd(filmId)) {
            throw new NotFoundException("Фильм не найден");
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<Film> getFamousFilms(Integer count) {
        if (count != null) {
            return genreStorage.setGenresInFilm(filmStorage.getFamousFilms(count));
        } else {
            return null;
        }
    }

    public List<Film> getSortedDirectorFilms(Integer directorId, String sortBy) {
        if (directorStorage.checkDirectorExistInDb(directorId)) {
            return genreStorage.setGenresInFilm(filmStorage.getSortedDirectorFilms(directorId, sortBy));
        } else {
            throw new NotFoundException("Режиссёр не найден");
        }
    }

    private boolean checkFilmAndLikeInExistInDb(int id, int userId) {
        return filmStorage.checkFilmExistInBd(id) && filmStorage.checkFilmExistInBd(userId);
    }

    private void validate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getDescription().length() > 200 ||
                film.getDuration() <= 0) {
            throw new ValidationException();
        }
    }
}
