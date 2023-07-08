package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmGenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmMpa.FilmMpaStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmMpaStorage filmMpaStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final String filmsSql = "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join film_mpas fm on f.id = fm.film_id " +
            "left join mpas m on fm.mpa_id = m.id";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMpaStorage filmMpaStorage, MpaStorage mpaStorage,
                         FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMpaStorage = filmMpaStorage;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    @Override
    public Film createFilm(Film film) {
        final String sql = "insert into films (name, release_date, description, duration, rate) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setObject(2, film.getReleaseDate());
            preparedStatement.setString(3, film.getDescription());
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getRate());

            return preparedStatement;
        }, generatedKeyHolder);

        int filmId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();

        film.setId(filmId);

        return addMpaAndGenres(film);
    }

    @Override
    public Film getFilmById(Integer filmId) {
        List<Film> films = jdbcTemplate.query(filmsSql.concat(" where f.id = ?"), new FilmMapper(), filmId);

        if (!films.isEmpty()) {
            Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);

            return films.get(0).toBuilder().genres(filmGenres).build();
        }

        return null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = jdbcTemplate.query(filmsSql, new FilmMapper());

        return setFilmGenres(films);
    }

    @Override
    public Film updateFilm(Film film) {
        final String sql = "update films set name = ?, release_date = ?, description = ?, duration = ?, " +
                "rate = ? where id = ?";

        jdbcTemplate.update(sql, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getRate(), film.getId());

        filmMpaStorage.deleteFilmMpaById(film.getId());
        filmGenreStorage.deleteAllFilmGenresById(film.getId());

        return addMpaAndGenres(film);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        final String sql = "select f.*, m.id as mpa_id, m.name as mpa_name from films f left join likes l on f.id = l.film_id " +
                "left join film_mpas fm on f.id = fm.film_id " +
                "left join mpas m on fm.mpa_id = m.id group by f.name, f.id " +
                "order by count(l.film_id) desc limit ?";
        Collection<Film> films = jdbcTemplate.query(sql, new FilmMapper(), count);

        return setFilmGenres(films);
    }

    private Collection<Film> setFilmGenres(Collection<Film> films) {
        Map<Integer, Collection<Genre>> filmGenresMap = filmGenreStorage.getAllFilmGenres(films);

        return films.stream().peek(film -> {
            if (Objects.nonNull(filmGenresMap.get(film.getId()))) {
                film.setGenres(filmGenresMap.get(film.getId()));
            }
        }).collect(Collectors.toList());
    }

    private Film addMpaAndGenres(Film film) {
        int filmId = film.getId();
        int mpaId = film.getMpa().getId();

        filmMpaStorage.addFilmMpa(filmId, mpaId);
        new LinkedHashSet<>(film.getGenres()).forEach(genre -> filmGenreStorage.addFilmGenre(filmId, genre.getId()));

        Mpa filmMpa = mpaStorage.getMpaById(mpaId);
        Collection<Genre> filmGenres = filmGenreStorage.getAllFilmGenresById(filmId);

        return film.toBuilder().id(filmId).mpa(filmMpa).genres(filmGenres).build();
    }
}
