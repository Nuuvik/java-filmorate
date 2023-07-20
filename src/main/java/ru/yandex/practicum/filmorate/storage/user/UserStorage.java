package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    boolean checkUserExistInBd(int id);

    User getUserById(int id);

    void addFriend(int userId, int friendId);

    List<Integer> getFriendsId(User user);

    List<User> getFriendsList(Integer userId);

    void deleteFriend(int userId, int friendId);

    void deleteUser(Integer id);
}
