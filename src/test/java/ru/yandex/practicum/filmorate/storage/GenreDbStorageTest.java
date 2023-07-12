package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmGenre.GenreDbStorage;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void getNotEmptyListGenresCallMethodGetAllGenre() {
        Assertions.assertFalse(genreDbStorage.getAllGenre().isEmpty());
    }

    @Test
    public void getGenreByIdCallMethodGetGenreById() {
        Assertions.assertEquals(Genre.builder().id(2).name("Драма").build(), genreDbStorage.getGenreById(2));
    }

    @Test
    public void getNotFoundExceptionCallMethodGetGenreById() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> genreDbStorage.getGenreById(9));
    }
}
