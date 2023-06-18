package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private final User user = User
            .builder()
            .login("login")
            .name("name")
            .email("mail@mail.com")
            .birthday(LocalDate.parse("2023-01-01"))
            .build();
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void shouldCreateUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotCreateUserIfLoginIsWrong() {
        String[] logins = {"login space", "", " ", null};

        Arrays.stream(logins).forEach(login -> {
            User userWithIncorrectLogin = user
                    .toBuilder()
                    .login(login)
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(userWithIncorrectLogin);

            assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldNotCreateUserIfEmailIsWrong() {
        String[] emails = {"mail.@mail.com", ".mail.@mail.com", "mail-mail@mail.com.", "mailmail.com", "", null};

        Arrays.stream(emails).forEach(email -> {
            User userWithIncorrectEmail = user
                    .toBuilder()
                    .email(email)
                    .build();

            Set<ConstraintViolation<User>> violations = validator.validate(userWithIncorrectEmail);

            assertFalse(violations.isEmpty());
        });
    }

    @Test
    void shouldNotCreateUserIfBirthdayIsWrong() {
        User userWithIncorrectBirthday = user
                .toBuilder()
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(userWithIncorrectBirthday);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}