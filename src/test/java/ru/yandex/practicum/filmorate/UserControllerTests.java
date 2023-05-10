package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest
class UserControllerTests {

    private UserStorage userStorage = new InMemoryUserStorage();
    private InMemoryUserService userService = new InMemoryUserService(userStorage);
    UserController userController = new UserController(userService);

    @Test
    void create() {
        User testedUser = User.builder()
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1964, 8, 20))
                .build();

        User expectedUser = User.builder()
                .id(1)
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1964, 8, 20))
                .friends(Collections.emptySet())
                .build();
        User resultUser = userController.create(testedUser);
        Assertions.assertEquals(expectedUser, resultUser);
    }

    @Test
    void createEmptyName() {
        User testedUser = User.builder()
                .email("friend@common.ru")
                .login("common")
                .birthday(LocalDate.of(2000, 8, 20))
                .build();

        User expectedUser = User.builder()
                .id(1)
                .email("friend@common.ru")
                .login("common")
                .name("common")
                .birthday(LocalDate.of(2000, 8, 20))
                .friends(Collections.emptySet())
                .build();

        User resultUser = userController.create(testedUser);
        Assertions.assertEquals(expectedUser, resultUser);
    }

}
