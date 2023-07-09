package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private Film film;
    private User user;

    @BeforeEach
    public void setUp() {
        film = Film.builder()
                .name("name")
                .description("desc")
                .releaseDate(LocalDate.of(1999, 8, 17))
                .duration(136)
                .build();
        film.setMpa(Mpa.builder()
                .id(1)
                .name("NC-17")
                .build());

        user = User.builder()
                .email("mail@mail.mail")
                .login("login")
                .birthday(LocalDate.of(1999, 8, 17))
                .build();
        user.setFriends(new HashSet<>());
    }

    @Test
    public void addFilmValid() {
        filmDbStorage.addFilm(film);
        Assertions.assertTrue(filmDbStorage.checkFilmExistInBd(film.getId()));
    }

    @Test
    public void updateFilmValid() {
        filmDbStorage.addFilm(film);
        film.setName("newName");
        film.setId(1);
        filmDbStorage.updateFilm(film);
        film.setName("name");

        Assertions.assertNotEquals(film, filmDbStorage.getFilmById(film.getId()));
    }

    @Test
    public void addLikeAndDeleteLikeValidIds() {
        filmDbStorage.addFilm(film);
        userDbStorage.addUser(user);
        filmDbStorage.addLike(1, 1);
        Assertions.assertFalse(filmDbStorage.getLikesByFilmId(1).isEmpty());
    }

}
