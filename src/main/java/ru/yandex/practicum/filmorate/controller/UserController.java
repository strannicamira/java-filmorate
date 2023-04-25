package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping(value = "/users")
    @ResponseBody
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping(value = "/users")
    @ResponseBody
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }
}
