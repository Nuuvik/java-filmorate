package ru.yandex.practicum.filmorate.storage.filmMpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaMapper;

@Component
@Qualifier("FilmMpaDbStorage")
public class FilmMpaDbStorage implements FilmMpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmMpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilmMpa(Integer filmId, Integer mpaId) {
        final String sql = "insert into film_mpas (film_id, mpa_id) values (?, ?)";

        jdbcTemplate.update(sql, filmId, mpaId);
    }

    @Override
    public Mpa getFilmMpaById(Integer filmId) {
        final String sql = "select m.id as id, name from film_mpas fm left join mpas m on fm.mpa_id = m.id where film_id = ?";

        return jdbcTemplate.queryForObject(sql, new MpaMapper(), filmId);
    }

    @Override
    public void deleteFilmMpaById(Integer filmId) {
        final String sql = "delete from film_mpas where film_id = ?";

        jdbcTemplate.update(sql, filmId);
    }
}
