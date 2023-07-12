package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {
    private final UserDbStorage userDbStorage;
    private User user;
    private User friend;

    @BeforeEach
    public void setUp() {

        user = User.builder()
                .email("mail@mail.mail")
                .login("login")
                .birthday(LocalDate.of(1999, 8, 17))
                .build();
        user.setFriends(new HashSet<>());

        friend = User.builder()
                .email("friend@mail.mail")
                .login("friend")
                .birthday(LocalDate.of(1979, 8, 17))
                .build();
        user.setFriends(new HashSet<>());
    }

    @Test
    public void createUserValidFields() {
        userDbStorage.addUser(user);
        Assertions.assertEquals(1, userDbStorage.getUserById(1).getId());
    }

    @Test
    public void updateUserValidFields() {
        userDbStorage.addUser(user);
        user.setName("newName");
        user.setId(1);
        userDbStorage.updateUser(user);
        user.setName("name");

        Assertions.assertNotEquals(user, userDbStorage.getUserById(user.getId()));
    }

    @Test
    void saveAndDeleteFriendsValidIds() {
        userDbStorage.addUser(user);
        userDbStorage.addUser(friend);
        userDbStorage.addFriend(user.getId(), friend.getId());
        Assertions.assertEquals(1, userDbStorage.getFriendsList(user.getId()).size());
        Assertions.assertEquals(0, userDbStorage.getFriendsList(friend.getId()).size());

        userDbStorage.deleteFriend(user.getId(), friend.getId());
        Assertions.assertEquals(0, userDbStorage.getFriendsList(user.getId()).size());
        Assertions.assertEquals(0, userDbStorage.getFriendsList(friend.getId()).size());
    }

}
