package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {

    User create(User user);

    User getById(Integer id);

    Map<Integer, User> getAll();

    User update(User user);

    void deleteById(Integer id);
}