package ru.yandex.practicum.filmorate.validator;


import ru.yandex.practicum.filmorate.annotation.Login;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

public class LoginValidator implements ConstraintValidator<Login, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.contains("") || value.contains(" ")) {
            throw new ValidationException("Неправильно введен логин");
        } else
            return true;


    }
}
