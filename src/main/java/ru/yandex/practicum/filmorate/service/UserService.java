package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService extends UserDao {
    void addFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriends(Integer id, Integer otherId);

    boolean deleteFriend(Integer userId, Integer friendId);
}
