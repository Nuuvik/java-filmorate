package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.GenreStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;

    public List<Film> getFilms() {
        return genreStorage.setGenreInFilm(filmStorage.getFilms());
    }

    public Film addFilm(Film film) {
        if (isValidation(film)) {
            return genreStorage.setGenreInFilm(List.of(filmStorage.addFilm(film))).get(0);
        } else {
            throw new ValidationException();
        }
    }

    public Film updateFilm(Film film) {
        if (isValidation(film)) {
            if (filmStorage.checkFilmExistInBd(film.getId())) {
                genreStorage.deleteFilmGenre(film);
                return genreStorage.setGenreInFilm(List.of(filmStorage.updateFilm(film))).get(0);
            } else {
                throw new NotFoundException("Фильм не найден");
            }
        } else {
            throw new ValidationException();
        }
    }

    public Film getFilmById(int filmId) {
        try {
            return genreStorage.setGenreInFilm(List.of(filmStorage.getFilmById(filmId))).get(0);
        } catch (Exception e) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public Film addLike(int filmId, int userId) {
        if (filmStorage.checkFilmExistInBd(filmId)) {
            filmStorage.addLike(userId, filmId);
            log.info("Добавлен лайк от - {} фильму - {}", userId, filmId);
            return genreStorage.setGenreInFilm(List.of(filmStorage.getFilmById(filmId))).get(0);
        } else {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public Film deleteLike(int filmId, int userId) {
        if (checkFilmAndLikeInExistInDb(filmId, userId)) {
            filmStorage.deleteLike(userId, filmId);
            log.info("Удален лайк - {} у фильму - {}", userId, filmId);
            return genreStorage.setGenreInFilm(List.of(filmStorage.getFilmById(filmId))).get(0);
        } else if (!filmStorage.checkFilmExistInBd(filmId)) {
            throw new NotFoundException("Фильм не найден");
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<Film> getFamousFilms(Integer count) {
        if (count != null) {
            return getFilms().stream()
                    .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private boolean checkFilmAndLikeInExistInDb(int id, int userId) {
        return filmStorage.checkFilmExistInBd(id) && filmStorage.checkFilmExistInBd(userId);
    }

    private boolean isValidation(Film film) throws ValidationException {
        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28)) &&
                film.getDescription().length() <= 200 &&
                film.getDuration() > 0) {
            return true;
        } else {
            throw new ValidationException();
        }
    }
}
