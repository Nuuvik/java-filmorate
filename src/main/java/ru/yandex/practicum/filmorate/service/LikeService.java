package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
public class LikeService {

    private final LikeStorage likeStorage;

    public LikeService(@Qualifier("LikeDbStorage") LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        likeStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeFromFilm(Integer filmId, Integer userId) {
        likeStorage.deleteLikeFromFilm(filmId, userId);
    }
}
