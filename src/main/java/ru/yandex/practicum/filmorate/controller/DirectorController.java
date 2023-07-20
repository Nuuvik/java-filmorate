package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @GetMapping
    public List<Director> getAllDirector() {
        return directorService.getDirectorsList();
    }

    @GetMapping("/{directorId}")
    public Director getDirector(@PathVariable Integer directorId) {
        return directorService.getDirectorById(directorId);
    }

    @DeleteMapping("/{directorId}")
    public String deleteDirector(@PathVariable Integer directorId) {
        return directorService.deleteDirector(directorId);
    }
}
