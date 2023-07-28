package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {

    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if (directorStorage.checkDirectorExistInDb(director.getId())) {
            return directorStorage.updateDirector(director);
        } else {
            throw new NotFoundException("Режиссёр не найден");
        }
    }

    public Director getDirectorById(Integer id) {
        if (directorStorage.checkDirectorExistInDb(id)) {
            return directorStorage.getDirectorById(id);
        } else {
            throw new NotFoundException("Режиссёр не найден");
        }
    }

    public List<Director> getDirectorsList() {
        return directorStorage.getDirectorsList();
    }

    public String deleteDirector(Integer id) {
        if (directorStorage.checkDirectorExistInDb(id)) {
            directorStorage.deleteDirector(id);
            return String.format("Режиссёр с id %s удалён", id);
        } else {
            throw new NotFoundException("Режиссёр не найден");
        }
    }
}
