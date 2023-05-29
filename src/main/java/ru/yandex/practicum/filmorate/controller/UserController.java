package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/users/{id}")
    public Optional<User> getUser(@PathVariable("id") Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId,
                          @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        return userService.getFriends(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriend(@PathVariable("id") Integer id,
                                      @PathVariable("otherId") Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable("id") Integer userId,
                                @PathVariable("friendId") Integer friendId) {
        return userService.deleteFriend(userId, friendId);
    }
}
