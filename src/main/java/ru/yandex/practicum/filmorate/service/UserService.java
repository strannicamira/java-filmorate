package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

public interface UserService extends UserStorage {
    boolean addFriend(Integer userId, Integer friendId);

    boolean deleteFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherId);
}
