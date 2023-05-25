package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {

    private User user;
    private UserController userController;

    @Test
    void shouldCreateUser() throws ValidationException {
        User user1 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        User user2 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        user2.setId(1);
        user2.setName("login");
        UserController userController = new UserController();
        User user3 = userController.create(user1);
        assertEquals(user2.getId(), user3.getId());
        assertEquals(user2.getName(), user3.getName());
        assertEquals(user2.getLogin(), user3.getLogin());
        assertEquals(user2.getEmail(), user3.getEmail());
        assertEquals(user2.getBirthday(), user3.getBirthday());
    }

    @Test
    void shouldCreateUserWithoutName() throws ValidationException {
        User user1 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        User user2 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        user2.setId(1);
        UserController userController = new UserController();
        User user3 = userController.create(user1);
        assertEquals(user2.getId(), user3.getId());
        assertEquals("login", user3.getName());
        assertEquals(user2.getLogin(), user3.getLogin());
        assertEquals(user2.getEmail(), user3.getEmail());
        assertEquals(user2.getBirthday(), user3.getBirthday());
    }

    @Test
    void shouldNotCreateUser() throws ValidationException {
        User invalidUser = new User();
        invalidUser.setName("");
        invalidUser.setLogin("");
        invalidUser.setEmail("");
        invalidUser.setBirthday(LocalDate.now());

        assertThrows(NullPointerException.class, () -> userController.create(invalidUser));
    }

    @Test
    void shouldUpdateUser() throws ValidationException {
        UserController userController = new UserController();
        User user1 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        userController.create(user1);
        User user2 = new User("newEmail@mail.com", "newLogin", LocalDate.of(2023, 4, 4));
        user2.setId(1);
        user2.setName("name");
        User user3 = userController.update(user2);
        assertEquals(user2.getId(), user3.getId());
        assertEquals(user2.getName(), user3.getName());
        assertEquals(user2.getLogin(), user3.getLogin());
        assertEquals(user2.getEmail(), user3.getEmail());
        assertEquals(user2.getBirthday(), user3.getBirthday());
    }

    @Test
    void shouldNotUpdateUser() {
        UserController userController = new UserController();
        User user1 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        userController.create(user1);
        User user2 = new User("newEmailmail.com", "newLogin", LocalDate.of(2024, 4, 4));
        user2.setId(999);


        assertThrows(UpdateException.class, () -> {
            userController.update(user2);
        });
    }

    @Test
    void findAll() throws ValidationException {
        UserController userController = new UserController();
        List<User> users = userController.findAll();
        assertEquals(0, users.size());
        User user1 = new User("email@mail.com", "login", LocalDate.of(2023, 1, 1));
        userController.create(user1);
        users = userController.findAll();
        assertEquals(1, users.size());
    }
}