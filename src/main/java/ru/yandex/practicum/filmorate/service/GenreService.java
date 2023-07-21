package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmGenre.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Genre getGenreById(int id) {
        try {
            return genreDbStorage.getGenreById(id);
        } catch (Exception e) {
            throw new NotFoundException("Жанр не найден");
        }
    }

    public List<Genre> getAllGenre() {
        try {
            return genreDbStorage.getAllGenre();
        } catch (Exception e) {
            throw new NotFoundException("Жанры не найдены");
        }
    }

}
