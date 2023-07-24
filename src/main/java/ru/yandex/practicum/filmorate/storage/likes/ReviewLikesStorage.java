package ru.yandex.practicum.filmorate.storage.likes;

public interface ReviewLikesStorage {

    void addLike(Integer reviewId, Integer userId, boolean isPositive);

    void deleteLike(Integer reviewId, Integer userId);
}
