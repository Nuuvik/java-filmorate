package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

@Component
public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    boolean checkFilmExistInBd(int id);

    Film getFilmById(int id);

    void deleteFilm(Integer id);

    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    List<Film> setLikesAndDirectorsInFilm(List<Film> films);

    Set<Integer> getLikesByFilmId(int filmId);

    List<Film> getSortedDirectorFilms(Integer directorId, String sortBy);

    List<Film> getFamousFilms(Integer count);

    List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year);

    List<Film> getPopularFilmsByGenre(Integer count, Integer genreId);

    List<Film> getPopularFilmsByYear(Integer count, Integer year);

    List<Film> searchFilmsByTitle(String title);

    List<Film> searchFilmsByDirector(String director);

    List<Film> searchFilmsByDirectorAndTitle(String query);

    List<Film> getCommonFilmsByPopularity(Integer userId, Integer friendId);
}
