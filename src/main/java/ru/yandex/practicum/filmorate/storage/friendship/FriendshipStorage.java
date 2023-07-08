package ru.yandex.practicum.filmorate.storage.friendship;

public interface FriendshipStorage {
    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);
}
