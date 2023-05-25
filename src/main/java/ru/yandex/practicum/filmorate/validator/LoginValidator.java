package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.Login;
import ru.yandex.practicum.filmorate.exception.ValidationException;

public class LoginValidator implements ConstraintValidator<Login, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.contains(" ")) {
            throw new ValidationException("Неправильно введен логин");
        } else
            return true;


    }
}
