package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final String NOT_FOUND_MESSAGE = "пользователя с id %s нет";
    private final InMemoryUserStorage userStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public User createUser(User user) {
        setUserName(user);

        return userStorage.create(user);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Integer id) {
        User user = userStorage.getById(id);

        checkUserIsNotNull(user, id);

        return user;
    }

    public List<User> getUserFriends(Integer id) {
        User user = userStorage.getById(id);

        checkUserIsNotNull(user, id);

        Map<Integer, User> users = getUsers();

        return user
                .getFriends()
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(User user1, User user2) {
        checkUserIsNotNull(user1, user1.getId());
        checkUserIsNotNull(user2, user2.getId());

        Map<Integer, User> users = getUsers();
        Set<Integer> user1Friends = user1.getFriends();
        Set<Integer> user2Friends = user2.getFriends();

        return user1Friends
                .stream()
                .filter(user2Friends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    public User updateUser(User user) {
        checkUserIsNotNull(user, user.getId());
        setUserName(user);
        return userStorage.update(user);
    }

    public void addFriendToUser(User user, User friend) {
        checkUserIsNotNull(user, user.getId());
        user.getFriends().add(friend.getId());
        userStorage.update(user);
    }

    public void deleteFriendFromUser(User user, User friend) {
        checkUserIsNotNull(user, user.getId());
        user.getFriends().remove(friend.getId());
    }

    private void checkUserIsNotNull(User user, Integer id) {
        if (UserValidator.isUserNotFound(getUsers(), user)) {
            throw new UpdateException(String.format(NOT_FOUND_MESSAGE, id));
        }
    }

    private void setUserName(User user) {
        if (!UserValidator.isUserNameValid(user.getName())) {
            user.setName(user.getLogin());
        }
    }
}