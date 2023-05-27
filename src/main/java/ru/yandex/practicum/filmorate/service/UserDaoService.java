package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dao.UserDao;

public interface UserDaoService extends UserDao {
    void addFriend(Integer userId, Integer friendId);
}
