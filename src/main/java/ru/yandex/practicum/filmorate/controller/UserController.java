package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Создается пользователь: {}", user);

        return userService.createUser(user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return new ArrayList<>(userService.getUsers().values());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Integer id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        User user1 = userService.getUserById(id);
        User user2 = userService.getUserById(otherId);

        return userService.getCommonFriends(user1, user2);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Обновляется пользователь: {}", user);

        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        User user = userService.getUserById(id);
        User friend = userService.getUserById(friendId);

        userService.addFriendToUser(user, friend);
        userService.addFriendToUser(friend, user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendFromUser(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        User user = userService.getUserById(id);
        User friend = userService.getUserById(friendId);

        userService.deleteFriendFromUser(user, friend);
        userService.deleteFriendFromUser(friend, user);
    }
}

