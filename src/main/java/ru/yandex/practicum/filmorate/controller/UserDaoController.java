package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDaoService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserDaoController {

    private final UserDaoService userDaoService;

    @GetMapping("/users")
    public List<User> findAll() {
        return userDaoService.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userDaoService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userDaoService.update(user);
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUser(@PathVariable("id") Integer id) {
        return userDaoService.findUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId,
                          @PathVariable("friendId") Integer friendId) {
        userDaoService.addFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        return userDaoService.getFriends(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriend(@PathVariable("id") Integer id,
                                      @PathVariable("otherId") Integer otherId) {
        return userDaoService.getCommonFriends(id, otherId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable("id") Integer userId,
                                @PathVariable("friendId") Integer friendId) {
        return userDaoService.deleteFriend(userId, friendId);
    }
}
