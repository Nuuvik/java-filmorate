package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static java.time.Month.DECEMBER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private User user;
    private final UserController userController;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email("asd@sad.ru")
                .login("asdada")
                .name("qwerty")
                .birthday(LocalDate.of(2007, 9, 1))
                .build();
    }

    @Test
    public void getValidationExceptionEmailWithoutDog() {
        user.setEmail("12323");
        assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void userCreateEmailWithDog() {
        userController.postUser(user);
        assertFalse(userController.getUsers().isEmpty());
    }

    @Test
    public void userNotCreateEmptyEmail() {
        user.setEmail(null);
        assertTrue(userController.getUsers().isEmpty());
    }

    @Test
    public void userNotCreateLoginWithWhitespace() {
        user.setLogin("asd asd");
        assertTrue(userController.getUsers().isEmpty());
    }

    @Test
    public void nameIsReplacedOnLoginEmptyName() {
        user.setName("");
        userController.postUser(user);
        assertEquals(user.getLogin(), userController.getUsers().get(0).getName());
    }

    @Test
    public void userNotCreateDateOfBirthInTheFuture() {
        user.setBirthday(LocalDate.of(2895, DECEMBER, 28));
        assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

}
