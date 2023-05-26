package ru.yandex.practicum.filmorate.validator;


import ru.yandex.practicum.filmorate.annotation.Login;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<Login, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.contains(" ")) {
            throw new ValidationException("Неправильно введен логин");
        } else
            return true;


    }
}
