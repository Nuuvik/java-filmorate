package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.likes.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;

    private final ReviewLikesStorage reviewLikesStorage;

    public Review addReview(Review review) {
        validateFilmId(review.getFilmId());
        validateUserId(review.getUserId());
        Review reviewWithId = reviewStorage.addReview(review);
        return getReviewById(reviewWithId.getReviewId());
    }

    public Review updateReview(Review review) {
        validateReviewId(review.getReviewId());
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Integer reviewId) {
        validateReviewId(reviewId);
        reviewStorage.deleteReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        validateReviewId(reviewId);
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewsForFilm(Integer filmId, Integer count) {
        if (count <= 0) {
            log.warn("Пользователь ввёл неверное значение количества выводимых строк = " + count);
            throw new ValidationException();
        }
        if (filmId == null) {
            return reviewStorage.getAllReviewsWithLimit(count);
        } else {
            validateFilmId(filmId);
            return reviewStorage.getReviewsForFilm(filmId, count);
        }
    }

    public void addLike(Integer reviewId, Integer userId, boolean isPositive) {
        validateReviewId(reviewId);
        validateUserId(userId);
        reviewLikesStorage.addLike(reviewId, userId, isPositive);
    }

    public void deleteLike(Integer reviewId, Integer userId) {
        validateReviewId(reviewId);
        validateUserId(userId);
        reviewLikesStorage.deleteLike(reviewId, userId);
    }

    private void validateReviewId(Integer reviewId) {
        if (reviewId <= 0) {
            log.warn("Пользователь ввёл неверный id отзыва = " + reviewId);
            throw new NotFoundException("Не найден id");
        }
    }

    private void validateFilmId(Integer filmId) {
        if (filmId <= 0) {
            log.warn("Пользователь ввёл неверный id фильма = " + filmId);
            throw new NotFoundException("Не найден id");
        }
    }

    private void validateUserId(Integer userId) {
        if (userId <= 0) {
            log.warn("Пользователь ввёл неверный id пользователя = " + userId);
            throw new NotFoundException("Не найден id");
        }
    }
}
