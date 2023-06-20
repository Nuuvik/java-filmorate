package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage extends Storage<User> {

    @Override
    public User create(User user) {
        user.setId(items.size() + 1);
        items.put(user.getId(), user);

        return user;
    }

    @Override
    public User getById(Integer id) {
        return items.get(id);
    }

    @Override
    public User update(User user) {
        items.put(user.getId(), user);

        return user;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }
}