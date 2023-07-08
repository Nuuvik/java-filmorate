package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("LikeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        final String sql = "insert into likes (film_id, user_id) values (?, ?)";

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        final String sql = "delete from likes where film_id = ? and user_id = ?";

        jdbcTemplate.update(sql, filmId, userId);
    }
}
