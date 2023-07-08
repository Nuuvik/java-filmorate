package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class GenreValidator {

    public static boolean isGenreNotFound(Collection<Genre> genres, Genre genre) {
        return Objects.isNull(genre) || Objects.isNull(genres.stream()
                .collect(Collectors.toMap(Genre::getId, g -> g)).get(genre.getId()));
    }
}
