package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
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

    public List<Film> getRecommendations(Integer userId, List<Film> allLikedFilms) {
        if (!storage.checkUserExistInBd(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<Film> userLikes;
        Map<Integer, List<Film>> usersAndLikes = new HashMap<>();
        for (Film film : allLikedFilms) {
            List<Integer> userLikesId = film.getLikes().stream().collect(Collectors.toList());
            for (Integer id : userLikesId) {
                if (!usersAndLikes.containsKey(id)) {
                    List<Film> likedFilms = new ArrayList<>();
                    likedFilms.add(film);
                    usersAndLikes.put(id, likedFilms);
                } else {
                    usersAndLikes.get(id).add(film);
                }
            }
        }
        userLikes = usersAndLikes.get(userId);
        if (userLikes == null) {
            log.info("У пользователя с id={} нет лайков", userId);
            return new ArrayList<>();
        }
        Map<Integer, Integer> frequencyLikes = new HashMap<>(); // userId/freq
        for (Map.Entry<Integer, List<Film>> entry : usersAndLikes.entrySet()) {
            if (entry.getKey().equals(userId)) {
                continue;
            }
            if (!frequencyLikes.containsKey(entry.getKey())) {
                frequencyLikes.put(entry.getKey(), 0);
            }
            Integer freq = frequencyLikes.get(entry.getKey());
            userLikes.stream().filter(film -> entry.getValue().contains(film))
                    .forEach(film -> frequencyLikes.put(entry.getKey(), freq + 1));
        }

        int maxFreq = 0;
        Integer id = null;
        for (Map.Entry<Integer, Integer> entry : frequencyLikes.entrySet()) {
            if (maxFreq < entry.getValue()) {
                maxFreq = entry.getValue();
                id = entry.getKey();
            }
        }
        if (maxFreq == 0) {
            log.info("У пользователя с id={} нет общих лайков с кем либо", userId);
            return new ArrayList<>();
        }
        return usersAndLikes.get(id).stream().filter(film -> !userLikes.contains(film)).collect(Collectors.toList());
    }
}
