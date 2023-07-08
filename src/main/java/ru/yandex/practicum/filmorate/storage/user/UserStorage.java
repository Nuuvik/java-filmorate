package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User createUser(User user);

    User getUserById(Integer id);

    Collection<User> getAllUsers();

    User updateUser(User user);

    Collection<User> getUserFriends(Integer userId);

    Collection<User> getCommonFriends(Integer user1Id, Integer user2Id);
}
