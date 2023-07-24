package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Отзыв с id = {} добавляется", review.getReviewId());
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Отзыв с id = {} обновляется", review.getReviewId());
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Integer reviewId) {
        reviewService.deleteReview(reviewId);
        log.info("Отзыв с id = {} удален", reviewId);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") Integer reviewId) {
        log.info("Отзыв с id = {} идет поиск", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviewsForFilm(@RequestParam(required = false) Integer filmId,
                                          @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviewsForFilm(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.addLike(reviewId, userId, true);
        log.info("добавлен лайк");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteLike(reviewId, userId);
        log.info("удален лайк");
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.addLike(reviewId, userId, false);
        log.info("добавлен дизлайк");
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteLike(reviewId, userId);
        log.info("удален дизлайк");
    }
}