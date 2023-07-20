package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public List<User> getUsers() {
        return storage.getUsers();
    }

    public User addUser(User user) {
        if (validation(user)) {
            return storage.addUser(user);
        } else {
            throw new ValidationException();
        }
    }

    public User updateUser(User user) {
        if (validation(user)) {
            if (storage.checkUserExistInBd(user.getId())) {
                return storage.updateUser(user);
            } else {
                throw new NotFoundException("Пользователь не найден");
            }
        } else {
            throw new ValidationException();
        }
    }


    public User getUserById(int id) {
        if (storage.checkUserExistInBd(id)) {
            return storage.getUserById(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public String deleteUser(Integer id) {
        if (storage.checkUserExistInBd(id)) {
            storage.deleteUser(id);
            return String.format("Пользователь с id %s удален", id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public List<User> getFriends(int id) {
        if (storage.checkUserExistInBd(id)) {
            return storage.getFriendsList(id);
        } else {
            throw new NotFoundException("Друг не найден");
        }
    }

    public User addFriend(Integer userId, Integer friendId) {
        if (checkUserAndFriend(userId, friendId)) {
            storage.addFriend(userId, friendId);
            log.info("Добавлен друг в персону - {}", userId);
            return storage.getUserById(userId);
        } else {
            throw new NotFoundException("Персона не найдена");
        }
    }

    public User deleteFriend(int userId, int friendId) {
        if (checkUserAndFriend(userId, friendId)) {
            storage.deleteFriend(userId, friendId);
            log.info("Удален друг в персоне - {}", userId);
            return storage.getUserById(userId);
        } else {
            throw new NotFoundException("Персона не найдена");
        }
    }

    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        if (checkUserAndFriend(firstUserId, secondUserId)) {
            Set<User> friendsFirstUser = new HashSet<>(storage.getFriendsList(firstUserId));
            friendsFirstUser.retainAll(storage.getFriendsList(secondUserId));
            List<User> mutualFriends = new ArrayList<>(friendsFirstUser);
            log.info("Выведен список общих друзей пользователей с id - {}, {}", firstUserId, secondUserId);
            return mutualFriends;
        } else {
            throw new NotFoundException("Персона не найдена");
        }
    }

    private boolean checkUserAndFriend(int id, int otherId) {
        return storage.checkUserExistInBd(id) && storage.checkUserExistInBd(otherId);
    }

    private boolean validation(User user) throws NullPointerException {
        if (user.getBirthday().isBefore(LocalDate.now()) && user.getEmail().contains("@")
                && !user.getLogin().isBlank()) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.info("Пользователь обновлен/создан {}", user);
            return true;
        } else {
            throw new ValidationException();
        }
    }
}
