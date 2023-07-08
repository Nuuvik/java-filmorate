package ru.yandex.practicum.filmorate.validator;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Objects;

@UtilityClass
public class UserValidator {

    public static boolean isUserNotFound(User user) {
        return Objects.isNull(user);
    }

    public static boolean isUserNameValid(String userName) {
        return Objects.nonNull(userName) && !userName.isEmpty() && !userName.isBlank();
    }
}
