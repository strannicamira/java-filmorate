package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> findAll();

    void create(User user);

    void update(User user);

    Optional<User> findUserById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer userId);

    List<User> getCommonFriends(Integer id, Integer otherId);

    boolean deleteFriend(Integer userId, Integer friendId);
}
