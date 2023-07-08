package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.validator.GenreValidator;

import java.util.Collection;

@Service
public class GenreService {

    private static final String NOT_FOUND_MESSAGE = "Жанра с id %s нет";
    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("GenreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(Integer id) {
        Genre genre = genreStorage.getGenreById(id);

        checkGenreIsNotNull(genre, id);

        return genre;
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    private void checkGenreIsNotNull(Genre genre, Integer id) {
        if (GenreValidator.isGenreNotFound(getAllGenres(), genre)) {
            throw new UpdateException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }
}
