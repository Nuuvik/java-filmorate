package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    protected int id = 0;


    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;

    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws UpdateException {
        log.info("Обновлен фильм: {}", film);
        if (FilmValidator.isFilmNotFound(films, film)) {
            throw new UpdateException("Такого фильма нет.");
        }

        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    Map<Integer, Film> getFilms() {
        return films;
    }

}
