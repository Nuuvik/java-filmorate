package ru.yandex.practicum.filmorate.validator;


import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate firstFilmDate = LocalDate.parse("1895-12-28");

        return date.isEqual(firstFilmDate) || date.isAfter(firstFilmDate);
    }
}