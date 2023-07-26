package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.SortException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmGenre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("films").usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMapFilms(film)).intValue());
        genreStorage.createFilmGenre(film);
        directorStorage.addDirectorToFilm(film);
        film.getGenres().clear();
        film.setMpa(getFilmById(film.getId()).getMpa());
        log.info("В базу добавлен фильм. id - {}", film.getId());
        return setLikesAndDirectorsInFilm(List.of(film)).get(0);
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
        directorStorage.addDirectorToFilm(film);
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
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms));
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
    public List<Film> setLikesAndDirectorsInFilm(List<Film> films) {
        List<Integer> filmIds = new ArrayList<>();
        films.forEach(film -> filmIds.add(film.getId()));
        String sqlQuery =
                "SELECT \n" +
                        "f.id , \n" +
                        "l.user_id, \n" +
                        "df.director_id, \n" +
                        "d.name\n" +
                        "FROM FILMS f \n" +
                        "LEFT JOIN LIKES l ON f.ID  = l.FILM_ID \n" +
                        "LEFT JOIN DIRECTOR_FILMS df ON f.ID  = df.FILM_ID \n" +
                        "LEFT JOIN DIRECTOR d ON df.director_id  = d.id \n" +
                        "where f.ID  IN (" + StringUtils.join(filmIds, ',') + ")" +
                        "order by user_id asc;";
        SqlRowSet likeAndDirectorRows = jdbcTemplate.queryForRowSet(sqlQuery);
        for (Film film : films) {
            film.getDirectors().clear();
        }
        while (likeAndDirectorRows.next()) {
            for (Film film : films) {
                if (film.getId() == likeAndDirectorRows.getInt("id")) {
                    film.getLikes().add(likeAndDirectorRows.getInt("user_id"));
                    if (likeAndDirectorRows.getInt("director_id") != 0) {
                        film.getDirectors().add(new Director(likeAndDirectorRows.getInt("director_id"),
                                likeAndDirectorRows.getString("name")));
                    }
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
        return setLikesAndDirectorsInFilm(List.of(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, filmId).get(0))).get(0);
    }

    @Override
    public void addLike(int userId, int filmId) {
        String sqlQuery = "INSERT into likes (film_id, user_id) values(?, ?)";
        addToFeedAddLike(userId, filmId);
        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
        } catch (RuntimeException e) {
            log.debug("Лайк уже существует");
        }

    }

    @Override
    public void deleteLike(int userId, int filmId) {
        String sqlQuery = "DELETE FROM likes WHERE user_id = ? and film_id = ?";
        addToFeedDeleteLike(userId, filmId);
        jdbcTemplate.update(sqlQuery, userId, filmId);

    }

    @Override
    public Set<Integer> getLikesByFilmId(int filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapLikes, filmId));
    }

    @Override
    public List<Film> getSortedDirectorFilms(Integer directorId, String sortBy) {
        String sqlQueryForEach = "SELECT f.*, m.name AS mpa_name FROM FILMS f " +
                "LEFT JOIN MPA m ON f.mpa_id = m.id " +
                "LEFT JOIN DIRECTOR_FILMS df ON f.id = df.film_id " +
                "LEFT JOIN DIRECTOR d ON df.director_id = d.id ";
        switch (sortBy) {
            case "year":
                String sqlQuery = sqlQueryForEach + " WHERE d.id = ? " +
                        "ORDER BY EXTRACT(YEAR FROM CAST(release_date AS date))";
                return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, directorId));
            case "likes":
                sqlQuery = sqlQueryForEach + "LEFT JOIN LIKES l ON f.id = l.film_id " +
                        "WHERE d.id = ? GROUP BY f.id " +
                        "ORDER BY COUNT(l.user_id) DESC";
                return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, directorId));
            default:
                throw new SortException("Не верный тип сортировки");
        }
    }

    @Override
    public List<Film> getFamousFilms(Integer count) {
        String sqlQuery = "select f.* , m.id as mpa_id, " +
                "m.name as mpa_name, g.id as genre_id,g.name as genre_name, " +
                "l.user_id as user_like_id " +
                "from films f  " +
                "left join mpa m on f.mpa_id = m.id  " +
                "left join film_genre fg ON f.id = fg.film_id " +
                "left join genre g on fg.genre_id = g.id " +
                "left join likes l on f.id = l.film_id " +
                "WHERE f.id IN (SELECT f.id AS likes_count FROM FILMS f LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.ID " +
                "ORDER BY COUNT(l.USER_ID) DESC) " +
                "LIMIT ?";

        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, count));
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "INNER JOIN film_genre fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC " +
                "LIMIT ?";
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, genreId, year, count));
    }

    @Override
    public List<Film> getPopularFilmsByGenre(Integer count, Integer genreId) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "INNER JOIN film_genre fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC " +
                "LIMIT ?";
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, genreId, count));
    }

    @Override
    public List<Film> getPopularFilmsByYear(Integer count, Integer year) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC " +
                "LIMIT ?";
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, year, count));
    }

    @Override
    public List<Film> getCommonFilmsByPopularity(Integer userId, Integer friendId) {
        String sqlQuery = "select f.* , m.id as mpa_id, m.name as mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m on f.mpa_id = m.id  " +
                "LEFT JOIN likes l on f.id = l.film_id " +
                "WHERE f.id IN (SELECT f.id FROM films f " +
                "LEFT JOIN likes lUser On lUser.film_id = f.id " +
                "LEFT JOIN likes lFriend On lFriend.film_id = f.id " +
                "WHERE lUser.user_id = ? and lFriend.user_id = ?) " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.user_id) DESC";
        return setLikesAndDirectorsInFilm(jdbcTemplate
                .query(sqlQuery, this::mapRowToFilms, userId, friendId));
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

    @Override
    public List<Film> searchFilmsByTitle(String title) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE lower(f.name) LIKE lower(?) " +
                "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC";
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, "%" + title + "%"));
    }

    @Override
    public List<Film> searchFilmsByDirector(String director) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN director_films df ON f.id = df.film_id " +
                "LEFT JOIN director d ON df.director_id = d.id " +
                "WHERE lower(d.name) LIKE lower(?) " +
                "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC";
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, "%" + director + "%"));
    }

    @Override
    public List<Film> searchFilmsByDirectorAndTitle(String query) {
        String sqlQuery = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "LEFT JOIN director_films df ON f.id = df.film_id " +
                "LEFT JOIN director d ON df.director_id = d.id " +
                "WHERE lower(f.name) LIKE lower(?) OR lower(d.name) LIKE lower(?) " +
                "ORDER BY (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) DESC";
        return setLikesAndDirectorsInFilm(jdbcTemplate.query(sqlQuery, this::mapRowToFilms, "%" + query + "%", "%" + query + "%"));
    }

    private void addToFeedDeleteLike(Integer userId, Integer filmId) {
        String sql = "INSERT INTO feed (user_id, event_type, operation,entity_id,time_stamp)" +
                " VALUES (?, 'LIKE', 'REMOVE', ?, ?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sql, userId, filmId, timestamp);
    }

    private void addToFeedAddLike(Integer userId, Integer filmId) {
        String sql = "INSERT INTO feed (user_id, event_type, operation,entity_id,time_stamp)" +
                " VALUES (?, 'LIKE', 'ADD', ?, ?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sql, userId, filmId, timestamp);
    }
}
