package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmGenre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("films").usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMapFilms(film)).intValue());
        genreStorage.createFilmGenre(film);

        film.getGenres().clear();
        film.setMpa(getFilmById(film.getId()).getMpa());
        log.info("В базу добавлен фильм. id - {}", film.getId());
        return setLikesInFilm(List.of(film)).get(0);
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        genreStorage.createFilmGenre(film);

        film.setMpa(getFilmById(film.getId()).getMpa());
        log.info("В базе обновлен фильм с id {}", film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery =
                "SELECT f.ID , \n" +
                        "f.NAME, \n" +
                        "f.DESCRIPTION,\n" +
                        "f.RELEASE_DATE, \n" +
                        "f.DURATION,\n" +
                        "m.ID as MPA_ID ,\n" +
                        "m.NAME as MPA_NAME\n" +
                        "FROM FILMS f \n" +
                        "JOIN MPA m ON f.MPA_ID  = m.ID";
        return setLikesInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms));
    }

    @Override
    public void deleteFilm(Integer id) {
        jdbcTemplate.update("DELETE FROM films WHERE id=?", id);
        log.info("Фильм с id {} удален", id);
    }

    @Override
    public boolean checkFilmExistInBd(int id) {
        String sqlQuery =
                "SELECT id\n" +
                        "FROM FILMS \n" +
                        "WHERE ID = ?;";
        return !jdbcTemplate.query(sqlQuery, this::mapFilmId, id).isEmpty();
    }

    @Override
    public List<Film> setLikesInFilm(List<Film> films) {
        List<Integer> filmIds = new ArrayList<>();
        films.forEach(film -> filmIds.add(film.getId()));
        String sql =
                "SELECT \n" +
                        "f.id , \n" +
                        "l.user_id\n" +
                        "FROM FILMS f \n" +
                        "JOIN LIKES l ON f.ID  = l.FILM_ID \n" +
                        "where f.ID  IN (" + StringUtils.join(filmIds, ',') + ")" +
                        "order by user_id asc;";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            for (Film film : films) {
                if (film.getId() == genreRows.getInt("id")) {
                    film.getLikes().add(genreRows.getInt("user_id"));
                }
            }
        }
        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery =
                "SELECT f.ID , \n" +
                        "f.NAME, \n" +
                        "f.DESCRIPTION,\n" +
                        "f.RELEASE_DATE, \n" +
                        "f.DURATION,\n" +
                        "m.ID as MPA_ID ,\n" +
                        "m.NAME as MPA_NAME\n" +
                        "FROM FILMS f \n" +
                        "JOIN MPA m ON f.MPA_ID  = m.ID\n" +
                        "WHERE f.ID = ?;";
        return setLikesInFilm(List.of(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, filmId).get(0))).get(0);
    }

    @Override
    public void addLike(int userId, int filmId) {
        String sqlQuery = "INSERT into likes (film_id, user_id) values(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int userId, int filmId) {
        String sql = "DELETE FROM likes WHERE user_id = ? and film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public Set<Integer> getLikesByFilmId(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, this::mapLikes, filmId));
    }

    private Integer mapFilmId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("id");
    }

    private Integer mapLikes(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("user_id");
    }

    private Film mapRowToFilms(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("mpa_id"))
                        .name(resultSet.getString("mpa_name"))
                        .build())
                .build();
        return film;
    }


    private Map<String, Object> toMapFilms(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }
}
