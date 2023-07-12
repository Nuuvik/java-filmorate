package ru.yandex.practicum.filmorate.storage.filmMpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?;";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
    }

    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
