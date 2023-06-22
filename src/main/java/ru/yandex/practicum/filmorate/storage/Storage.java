package ru.yandex.practicum.filmorate.storage;

import java.util.HashMap;
import java.util.Map;

public abstract class Storage<T> {

    protected final Map<Integer, T> items = new HashMap<>();

    public abstract T create(T item);

    public abstract T getById(Integer id);

    public Map<Integer, T> getAll() {
        return new HashMap<>(items);
    }

    public abstract T update(T item);

    public abstract void deleteById(Integer id);
}