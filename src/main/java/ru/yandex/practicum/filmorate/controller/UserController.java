package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();
    private Integer idCounter = 0;

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    @ResponseBody
    public User create(@Valid @RequestBody User user) {
        String userName;
        if (user.getName() == null || user.getName().isEmpty()) {
            userName = user.getLogin();
        } else {
            userName = user.getName();
        }
        final User resultUser = User.builder()
                .id(++idCounter)
                .email(user.getEmail())
                .name(userName)
                .login(user.getLogin())
                .birthday(user.getBirthday())
                .build();
        users.put(resultUser.getId(), resultUser);
        log.debug("Пользователь создан: '{}'", resultUser);
        return resultUser;
    }

    @PutMapping(value = "/users")
    @ResponseBody
    public User update(@Valid @RequestBody User user) {
        if (user != null && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Пользователь обновлен: '{}'", user);
        } else {
            throw new ValidationException();
        }
        return user;
    }
}
