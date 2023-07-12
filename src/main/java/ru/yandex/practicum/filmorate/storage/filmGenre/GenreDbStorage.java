package ru.yandex.practicum.filmorate.storage.filmGenre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        String sql =
                "SELECT g.ID ," +
                        "g.NAME " +
                        "FROM GENRE g " +
                        "WHERE g.ID = ?;";
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
    }

    @Override
    public Film setGenreInFilm(Film film) {
        List<Film> films = setGenresInFilm(List.of(film));
        return films.get(0);
    }

    public List<Film> setGenresInFilm(List<Film> films) {
        List<Integer> filmIds = new ArrayList<>();
        films.forEach(film -> filmIds.add(film.getId()));
        String sql =
                "SELECT \n" +
                        "fg.film_id, \n" +
                        "g.id, \n" +
                        "g.name \n" +
                        "FROM GENRE g  \n" +
                        "JOIN FILM_GENRE fg ON fg.GENRE_ID = g.ID \n" +
                        "where fg.film_Id IN (" + StringUtils.join(filmIds, ',') + ")" +
                        "ORDER BY g.id asc;";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            log.info("строка - {}", genreRows.getRow());
            for (Film film : films) {
                if (film.getId() == genreRows.getInt("film_id")) {
                    film.getGenres().add(Genre.builder()
                            .id(genreRows.getInt("id"))
                            .name(genreRows.getString("name")).build());
                }
            }
        }
        return films;
    }

    @Override
    public List<Genre> getAllGenre() {
        String sqlQuery = "SELECT * FROM genre GROUP BY ID ORDER BY ID ASC;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public void createFilmGenre(Film film) {
        String sqlQuery = "INSERT into film_genre (film_id, genre_id) values(?, ?);";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void deleteFilmGenre(Film film) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?;";
        jdbcTemplate.update(sql, film.getId());
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
