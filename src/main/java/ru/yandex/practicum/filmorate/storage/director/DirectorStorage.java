package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Integer directorId);

    List<Director> getDirectorsList();

    Director getDirectorById(Integer directorId);

    boolean checkDirectorExistInDb(Integer id);

    void addDirectorToFilm(Film film);

    void deleteDirectorsFromFilm(Integer directorId);
}
