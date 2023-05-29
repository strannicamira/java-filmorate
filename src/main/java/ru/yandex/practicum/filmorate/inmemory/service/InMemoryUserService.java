package ru.yandex.practicum.filmorate.inmemory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.inmemory.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        Map<Integer, User> users = userStorage.getUsers();
        if (userId != null && users.containsKey(userId) && friendId != null && users.containsKey(friendId)) {
            if (users.get(userId).getFriends().add(friendId) &&
                    users.get(friendId).getFriends().add(userId)) {
                log.debug("Друг добавлен: '{}'", users.get(userId));
                return users.get(userId);
            }
        } else {
            throw new NotFoundException("Пользователь не найден в списке.");
        }
        return null;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        Map<Integer, User> users = userStorage.getUsers();
        if (findUserById(userId).getFriends().remove(friendId) &&
                findUserById(friendId).getFriends().remove(userId)) {
            log.debug("Друг удален: '{}'", users.get(userId));
            return findUserById(userId);
        } else {
            throw new NotFoundException("Пользователя не удалось удалить " +
                    "из друзей.");
        }
    }

    @Override
    public List<User> getFriends(Integer userId) {
        List<User> friends = new ArrayList<>();
        Map<Integer, User> users = userStorage.getUsers();
        for (Integer id : findUserById(userId).getFriends()) {
            friends.add(users.get(id));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        return userStorage.getCommonFriends(userId, otherId);
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
