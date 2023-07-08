package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Objects;

@UtilityClass
public class FilmValidator {

    public static boolean isFilmNull(Film film) {
        return Objects.isNull(film);
    }
}
