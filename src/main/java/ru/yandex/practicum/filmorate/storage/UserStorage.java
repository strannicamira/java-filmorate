package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    User findUserById(Integer id);

    List<User> getCommonFriends(Integer userId, Integer otherId);

}
