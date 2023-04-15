package ru.yandex.practicum.filmorate.сontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.HashMap;

@RestController
@Slf4j
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();
    private Integer idCounter = 0;

    @GetMapping("/users")
    public HashMap<Integer, User> findAll() {
        return users;
    }

    @PostMapping(value = "/users")
    @ResponseBody
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(++idCounter);
        users.put(user.getId(), user);
        log.debug("Пользователь создан: '{}'", user);
        return user;
    }

    @PutMapping(value = "/users")
    @ResponseBody
    public User update(@Valid @RequestBody User user) {
        users.put(user.getId(), user);
        log.debug("Пользователь обновлен: '{}'", user);
        return user;
    }
}
