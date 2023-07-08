package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserStorageTest {

    private final UserStorage userStorage;
    private final User user = new User()
            .toBuilder()
            .name("Игорь")
            .login("pwned")
            .birthday(LocalDate.now())
            .email("igor@what.eng")
            .build();

    @Test
    public void createUser() {
        User newUser = userStorage.createUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", newUser.getId());
        assertThat(newUser).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(newUser).hasFieldOrPropertyWithValue("login", user.getLogin());
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", user.getBirthday());
        assertThat(newUser).hasFieldOrPropertyWithValue("email", user.getEmail());

    }

    @Test
    public void getUserById() {
        User newUser = userStorage.createUser(user);
        User userById = userStorage.getUserById(newUser.getId());

        assertThat(userById).hasFieldOrPropertyWithValue("id", newUser.getId());
        assertThat(userById).hasFieldOrPropertyWithValue("name", newUser.getName());
        assertThat(userById).hasFieldOrPropertyWithValue("login", newUser.getLogin());
        assertThat(userById).hasFieldOrPropertyWithValue("birthday", newUser.getBirthday());
        assertThat(userById).hasFieldOrPropertyWithValue("email", newUser.getEmail());
    }

    @Test
    public void getAllUsers() {
        userStorage.createUser(user);
        userStorage.createUser(user);

        Collection<User> users = userStorage.getAllUsers();

        assertEquals(users.size(), 2);
    }

    @Test
    public void updateUser() {
        User updatedUser = user.toBuilder().name("Василий").build();
        User updatedUserFromBD = userStorage.updateUser(updatedUser);

        assertThat(updatedUserFromBD).hasFieldOrPropertyWithValue("id", updatedUser.getId());
        assertThat(updatedUserFromBD).hasFieldOrPropertyWithValue("name", updatedUser.getName());
        assertThat(updatedUserFromBD).hasFieldOrPropertyWithValue("login", updatedUser.getLogin());
        assertThat(updatedUserFromBD).hasFieldOrPropertyWithValue("birthday", updatedUser.getBirthday());
        assertThat(updatedUserFromBD).hasFieldOrPropertyWithValue("email", updatedUser.getEmail());
    }
}
