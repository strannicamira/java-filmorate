package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDaoServiceImpl implements UserDaoService {
    private final UserDao userDao;

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public void create(User user) {
        userDao.create(user);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userDao.findUserById(id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        userDao.addFriend(userId,friendId);
    }
}
