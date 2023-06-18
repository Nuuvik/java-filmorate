package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(films.size() + 1);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
    }

    @Override
    public Map<Integer, Film> getAll() {
        return films;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public void deleteById(Integer id) {
        films.remove(id);
    }
}