package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        Map<Integer, User> users = userStorage.getUsers();
        if (userId != null && users.containsKey(userId) && friendId != null && users.containsKey(friendId)) {
            return users.get(userId).getFriends().add(friendId) &&
                    users.get(friendId).getFriends().add(userId);
        } else {
            throw new NotFoundException("Пользователь не найден в списке.");
        }
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        Map<Integer, User> users = userStorage.getUsers();
        if (userId != null && users.containsKey(userId) && friendId != null && users.containsKey(friendId)) {
            return users.get(userId).getFriends().remove(friendId) &&
                    users.get(friendId).getFriends().remove(userId);
        } else {
            throw new NotFoundException("Пользователь не найден в списке.");
        }

    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = Collections.emptyList();
        Map<Integer, User> users = userStorage.getUsers();
        if (userId != null && users.containsKey(userId)) {
            for (Integer id : users.get(userId).getFriends()) {
                friends.add(users.get(id));
            }
            return friends;
        } else {
            throw new NotFoundException("Пользователь не найден в списке.");
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<User> friends = Collections.emptyList();
        Map<Integer, User> users = userStorage.getUsers();
        if (userId != null && users.containsKey(userId) && otherId != null && users.containsKey(otherId)) {
            for (Integer id : userStorage.getUsers().get(userId).getFriends()) {
                for (Integer other :
                        userStorage.getUsers().get(otherId).getFriends())
                    if (id == other) {
                        friends.add(userStorage.getUsers().get(id));
                    }
            }
            return friends;
        } else {
            throw new NotFoundException("Пользователь не найден в списке.");
        }

    }

    @Override
    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public User findUserById(Integer id) {
        return userStorage.findUserById(id);
    }
}
