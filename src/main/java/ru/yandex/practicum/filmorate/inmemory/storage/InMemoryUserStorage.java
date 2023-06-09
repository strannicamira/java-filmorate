package ru.yandex.practicum.filmorate.inmemory.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {


    private Map<Integer, User> users = new ConcurrentHashMap<>();
    private Integer idCounter = 0;

    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public List<User> findAll() {

        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        String userName = user.getName();
        if (!StringUtils.hasText(userName)) {
            userName = user.getLogin();
        }

        final User resultUser = User.builder()
                .id(++idCounter)
                .email(user.getEmail())
                .name(userName)
                .login(user.getLogin())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
        users.put(resultUser.getId(), resultUser);
        log.debug("Пользователь создан: '{}'", resultUser);
        return resultUser;
    }

    @Override
    public User update(User user) {
        if (user != null && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Пользователь обновлен: '{}'", user);
        } else {
            throw new NotFoundException("Пользователь не обновлен. Не " +
                    "найден в списке.");
        }
        return user;
    }

    @Override
    public User findUserById(Integer id) {
        if (id != null && users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь не найден в списке.");
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        List<User> friends = new ArrayList<>();
        for (Integer id : findUserById(userId).getFriends()) {
            for (Integer other : findUserById(otherId).getFriends())
                if (id == other) {
                    friends.add(users.get(id));
                }
        }
        return friends;
    }
}
