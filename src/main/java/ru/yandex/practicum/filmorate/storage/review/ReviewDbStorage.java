package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Review addReview(Review review) {
        if (!dbContainsFilm(review.getFilmId())) {
            String message = "Невозможно создать отзыв к фильму, которого не существует";
            throw new NotFoundException(message);
        }
        if (!dbContainsUser(review.getUserId())) {
            String message = "Невозможно создать отзыв от пользователя, которого не существует";
            throw new NotFoundException(message);
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(toMap(review)).intValue());
        addToFeedReviewCreate(review.getReviewId(), review.getUserId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        if (!dbContainsReview(review.getReviewId())) {
            String message = "Ошибка запроса обновления отзыва." +
                    " Невозможно обновить отзыв которого не существует.";
            throw new NotFoundException(message);
        }
        String sql = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        review = getReviewById(review.getReviewId());
        addToFeedReviewUpdate(review.getReviewId());
        return review;
    }

    @Override
    public void deleteReview(Integer reviewId) {
        if (!dbContainsReview(reviewId)) {
            String message = "Ошибка запроса удаления отзыва." +
                    " Невозможно удалить отзыв которого не существует.";
            throw new NotFoundException(message);
        }
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        Review review = getReviewById(reviewId);
        addToFeedReviewDelete(reviewId, review.getUserId());
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        if (!dbContainsReview(reviewId)) {
            String message = "Ошибка запроса отзыва." +
                    " Невозможно запросить отзыв которого не существует.";
            throw new NotFoundException(message);
        }
        String sql = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USERS_ID, R.FILMS_id, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE R.REVIEW_ID = ? " +
                "GROUP BY R.REVIEW_ID";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReview(rs), reviewId);
    }

    @Override
    public List<Review> getReviewsForFilm(Integer filmId, Integer count) {
        if (!dbContainsFilm(filmId)) {
            String message = "Ошибка запроса фильма." +
                    " Невозможно запросить фильм которого не существует.";
            throw new NotFoundException(message);
        }
        String sql = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USERS_ID, R.FILMS_id, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE R.films_id = ? " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public List<Review> getAllReviewsWithLimit(Integer count) {
        String sql = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USERS_ID, R.FILMS_id, " +
                "(SUM(CASE WHEN RL.IS_POSITIVE = TRUE THEN 1 ELSE 0 END) - " +
                "SUM(CASE WHEN RL.IS_POSITIVE = FALSE THEN 1 ELSE 0 END)) AS USEFUL " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN REVIEW_LIKES AS RL ON R.REVIEW_ID = RL.REVIEW_ID " +
                "GROUP BY R.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
    }

    private boolean dbContainsReview(Integer reviewId) {
        String sql = "SELECT R.REVIEW_ID, R.CONTENT, R.IS_POSITIVE, R.USERS_ID, R.FILMS_id, " +
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

    private boolean dbContainsFilm(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean dbContainsUser(Integer userId) {
        if (userStorage.checkUserExistInBd(userId)) {
            return true;
        } else {
            return false;
        }
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

    public Map<String, Object> toMap(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("is_positive", review.getIsPositive());
        values.put("users_id", review.getUserId());
        values.put("films_id", review.getFilmId());
        return values;
    }

    private void addToFeedReviewUpdate(Integer reviewId) {
        String sqlQuery = "INSERT INTO feed (user_id, event_type, operation,entity_id,time_stamp) " +
                "VALUES (?, 'REVIEW', 'UPDATE', ?,?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sqlQuery, getReviewById(reviewId).getUserId(),
                reviewId, timestamp);
    }

    private void addToFeedReviewCreate(Integer reviewId, Integer userId) {
        String sql = "INSERT INTO feed (user_id, event_type, operation,entity_id,time_stamp) " +
                "VALUES (?, 'REVIEW', 'ADD', ?,?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sql, userId, reviewId, timestamp);
    }

    private void addToFeedReviewDelete(Integer reviewId, Integer userId) {
        String sqlQuery = "INSERT INTO feed (user_id, event_type, operation,entity_id,time_stamp)" +
                " VALUES (?, 'REVIEW', 'REMOVE', ?,?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sqlQuery, userId, reviewId, timestamp);
    }
}
