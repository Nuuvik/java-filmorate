package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmStorageTest {

    private final FilmStorage filmStorage;
    private final Film film = new Film()
            .toBuilder()
            .name("Фильм")
            .description("Интересный")
            .releaseDate(LocalDate.now())
            .duration(100)
            .mpa(new Mpa(1, null))
            .rate(5)
            .genres(List.of(new Genre(1, null)))
            .build();
    private Film newFilm;

    @BeforeEach
    public void addFilm() {
        newFilm = filmStorage.createFilm(film);
    }

    @Test
    public void createFilm() {
        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1);
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", film.getName());
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", film.getDescription());
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate());
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", film.getDuration());
        assertThat(newFilm).hasFieldOrPropertyWithValue("rate", film.getRate());

        assertEquals(newFilm.getGenres().size(), 1);
        assertEquals(new ArrayList<>(newFilm.getGenres()).get(0).getName(), "Комедия");
        assertEquals(newFilm.getMpa().getId(), film.getMpa().getId());
        assertEquals(newFilm.getMpa().getName(), "G");
    }

    @Test
    public void getFilmById() {
        Film filmFromBD = filmStorage.getFilmById(newFilm.getId());

        assertThat(filmFromBD).hasFieldOrPropertyWithValue("id", 1);
        assertThat(filmFromBD).hasFieldOrPropertyWithValue("name", newFilm.getName());
        assertThat(filmFromBD).hasFieldOrPropertyWithValue("description", newFilm.getDescription());
        assertThat(filmFromBD).hasFieldOrPropertyWithValue("releaseDate", newFilm.getReleaseDate());
        assertThat(filmFromBD).hasFieldOrPropertyWithValue("duration", newFilm.getDuration());
        assertThat(filmFromBD).hasFieldOrPropertyWithValue("rate", newFilm.getRate());

        assertEquals(filmFromBD.getGenres().size(), 1);
        assertEquals(new ArrayList<>(filmFromBD.getGenres()).get(0).getName(), "Комедия");
        assertEquals(filmFromBD.getMpa().getId(), newFilm.getMpa().getId());
        assertEquals(filmFromBD.getMpa().getName(), "G");
    }

    @Test
    public void getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();

        assertEquals(films.size(), 1);
    }

    @Test
    public void updateFilm() {
        Film updatedFilm = filmStorage.updateFilm(newFilm.toBuilder().name("Фильм!").build());

        assertThat(updatedFilm).hasFieldOrPropertyWithValue("id", 1);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", "Фильм!");
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("description", newFilm.getDescription());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("releaseDate", newFilm.getReleaseDate());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("duration", newFilm.getDuration());
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("rate", newFilm.getRate());

        assertEquals(updatedFilm.getGenres().size(), 1);
        assertEquals(new ArrayList<>(updatedFilm.getGenres()).get(0).getName(), "Комедия");
        assertEquals(updatedFilm.getMpa().getId(), newFilm.getMpa().getId());
        assertEquals(updatedFilm.getMpa().getName(), "G");
    }
}
