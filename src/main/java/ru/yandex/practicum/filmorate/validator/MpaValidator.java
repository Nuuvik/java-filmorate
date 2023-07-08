package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class MpaValidator {

    public static boolean isMpaNotFound(Collection<Mpa> mpas, Mpa mpa) {
        return Objects.isNull(mpa) || Objects.isNull(mpas.stream()
                .collect(Collectors.toMap(Mpa::getId, m -> m)).get(mpa.getId()));
    }
}
