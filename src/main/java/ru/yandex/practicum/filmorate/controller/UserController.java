package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class UserController {

    private Map<Integer, User> users = new ConcurrentHashMap<>();
    private Integer idCounter = 0;

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    @ResponseBody
    public User create(@Valid @RequestBody User user) {
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
            throw new ValidationException("Пользователь не обновлен. Не " +
                    "найден в списке.");
        }
        return user;
    }
}
