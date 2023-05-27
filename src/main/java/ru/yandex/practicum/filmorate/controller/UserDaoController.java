package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDaoServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserDaoController {

    private final UserDaoServiceImpl userDaoServiceImpl;

    @GetMapping("/users")
    public List<User> findAll() {
        return (List<User>) userDaoServiceImpl.findAll();
    }

    @PostMapping(value = "/users")
    public void create(@Valid @RequestBody User user) {
        userDaoServiceImpl.create(user);
    }

    @PutMapping(value = "/users")
    public void update(@Valid @RequestBody User user) {
        userDaoServiceImpl.update(user);
    }

}
