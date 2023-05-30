package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    protected int id = 0;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Запрос на добавление пользователя: {}", user);
        user.setId(++id);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws UpdateException {
        log.debug("Запрос на обновление пользователя: {}", user);
        if (UserValidator.isUserNotFound(users, user)) {
            throw new UpdateException("Такого пользователя нет.");
        }


        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    Map<Integer, User> getUsers() {
        return users;
    }
}

