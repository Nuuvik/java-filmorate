package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private Film film;
    private final FilmController filmController;

    @BeforeEach
    public void setUp() {
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(2007, 9, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).build())
                .build();
    }

    @Test
    public void nameCannotBeEmpty() {
        assertThrows(NullPointerException.class, () -> film.setName(null));
    }

    @Test
    public void filmMustBeCreatedWith200Desc() {
        film.setDescription(RandomStringUtils.random(200));
        filmController.postFilm(film);
        assertFalse(filmController.getFilms().isEmpty());
    }

    @Test
    public void filmMustBeNotCreatedWith201Desc() {
        film.setDescription(RandomStringUtils.random(201));
        assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void releaseDate1895NotValid() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void releaseDate1896IsValid() {
        film.setReleaseDate(LocalDate.of(1896, 12, 28));
        filmController.postFilm(film);
        assertFalse(filmController.getFilms().isEmpty());
    }

    @Test
    public void durationIsNegativeNotValid() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void durationIsNegativeIsValid() {
        film.setDuration(1);
        filmController.postFilm(film);
        assertFalse(filmController.getFilms().isEmpty());
    }
}
