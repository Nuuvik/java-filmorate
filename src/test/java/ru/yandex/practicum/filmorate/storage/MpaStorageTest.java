package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmMpa.MpaDbStorage;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void getAllMpaCallMethodGetAllMpa() {
        Assertions.assertEquals(5, mpaDbStorage.getAllMpa().size());
    }

    @Test
    public void getMpaByIdMpaExistInBd() {
        Assertions.assertEquals(Mpa.builder().id(2).name("PG").build(), mpaDbStorage.getMpaById(2));
    }

    @Test
    public void getNotFoundExceptionMpaNotExistInBd() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> mpaDbStorage.getMpaById(9));
    }
}
