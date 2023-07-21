package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("directorDbStorage")
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director").usingGeneratedKeyColumns("id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(Map.of("name", director.getName())).intValue());
        log.info("В базу добавлен новый режиссёр с id - {}", director.getId());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE director SET  name = ? WHERE id = ?;",
                director.getName(),
                director.getId());
        log.info("Режиссёр обновлен. id - {}", director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Integer directorId) {
        jdbcTemplate.update("DELETE FROM director WHERE id=?", directorId);
        log.info("Режиссёр с id {} удален", directorId);
    }

    @Override
    public List<Director> getDirectorsList() {
        return jdbcTemplate.query("SELECT * FROM director", this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        return jdbcTemplate.queryForObject("SELECT * FROM director WHERE id = ?",
                this::mapRowToDirector, directorId);
    }

    @Override
    public boolean checkDirectorExistInDb(Integer id) {
        String sqlQuery = "SELECT * FROM director WHERE id = ?";
        return !jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id).isEmpty();
    }

    @Override
    public void addDirectorToFilm(Film film) {
        if (film.getDirectors() != null) {
            String sqlQuery = "INSERT into director_films (film_id, director_id) values(?, ?);";
            if (!film.getDirectors().isEmpty()) {
                for (Director director : film.getDirectors()) {
                    jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
                }
            }
        }
    }

    @Override
    public void deleteDirectorsFromFilm(Integer filmId) {
        jdbcTemplate.update("DELETE FROM DIRECTOR_FILMS WHERE film_id = ?", filmId);
    }

    public Director mapRowToDirector(ResultSet resultSet, int row) throws SQLException {
        Director director = Director.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("Name"))
                .build();
        return director;
    }
}
