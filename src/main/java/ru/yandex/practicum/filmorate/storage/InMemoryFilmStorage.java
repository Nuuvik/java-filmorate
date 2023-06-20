package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage extends Storage<Film> {

    @Override
    public Film create(Film film) {
        film.setId(items.size() + 1);
        items.put(film.getId(), film);

        return film;
    }

    @Override
    public Film getById(Integer id) {
        return items.get(id);
    }

    @Override
    public Film update(Film film) {
        items.put(film.getId(), film);

        return film;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }
}