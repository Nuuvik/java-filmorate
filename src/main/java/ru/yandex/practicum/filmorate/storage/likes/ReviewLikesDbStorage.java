package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer reviewId, Integer userId, boolean isPositive) {
        if (!dbContainsReview(reviewId)) {
            String message = "Ошибка запроса добавления лайка." +
                    " Невозможно добавить лайк к отзыву, которого не существует.";
            throw new NotFoundException(message);
        }
        if (!dbContainsUser(userId)) {
            String message = "Ошибка запроса удаления лайка." +
                    " Невозможно добавить лайк пользователя, которого не существует.";
            throw new NotFoundException(message);
        }
        String sql = "INSERT INTO REVIEW_LIKES (REVIEW_ID, USERS_ID, IS_POSITIVE) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isPositive);
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        if (!dbContainsReview(reviewId)) {
            String message = "Ошибка запроса удаления лайка." +
                    " Невозможно удалить лайк к отзыву, которого не существует.";
            throw new NotFoundException(message);
        }
        if (!dbContainsUser(userId)) {
            String message = "Ошибка запроса удаления лайка." +
                    " Невозможно удалить лайк пользователя, которого не существует.";
            throw new NotFoundException(message);
        }
        String sql = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USERS_ID = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    private boolean dbContainsUser(Integer userId) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private boolean dbContainsReview(Integer reviewId) {
        String sql = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USERS_ID, R.FILMS_ID, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ? " +
                "GROUP BY R.REVIEW_ID";
        try {
            jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReview(rs), reviewId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        int id = rs.getInt("review_id");
        String content = rs.getString("content");
        boolean isPositive = rs.getBoolean("is_positive");
        Integer userId = rs.getInt("users_id");
        Integer filmId = rs.getInt("films_id");
        Integer useful = rs.getInt("useful");
        return new Review(id, content, isPositive, userId, filmId, useful);
    }
}
