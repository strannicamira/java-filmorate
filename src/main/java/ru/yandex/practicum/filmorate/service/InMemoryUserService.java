package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public boolean addFriend(Integer userId, Integer friendId) {
        return userStorage.getUsers().get(userId).getFriends().add(friendId) &&
                userStorage.getUsers().get(friendId).getFriends().add(userId);
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        return userStorage.getUsers().get(userId).getFriends().remove(friendId) &&
                userStorage.getUsers().get(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        for (Integer id : userStorage.getUsers().get(userId).getFriends()) {
            friends.add(userStorage.getUsers().get(id));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<User> friends = new ArrayList<>();
        for (Integer id : userStorage.getUsers().get(userId).getFriends()) {
            for (Integer other :
                    userStorage.getUsers().get(otherId).getFriends())
                if (id == other) {
                    friends.add(userStorage.getUsers().get(id));
                }
        }
        return friends;
    }
}
