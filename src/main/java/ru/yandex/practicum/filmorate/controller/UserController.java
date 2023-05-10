package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return (List<User>) userStorage.findAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") Integer id){
        return userStorage.findUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public boolean addFriend(@PathVariable("id") Integer userId,
                       @PathVariable("friendId") Integer friendId) {
        return userService.addFriend(userId,friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable("id") Integer userId,
                          @PathVariable("friendId") Integer friendId) {
        return userService.deleteFriend(userId,friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer userId){
        return userService.getFriends(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriend(@PathVariable("id") Integer id,
                                      @PathVariable("otherId") Integer otherId) {
        return userService.getCommonFriends(id,otherId);
    }
}
