package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.сontroller.UserController;

import java.time.LocalDate;

@SpringBootTest
class UserControllerTests {

    UserController userController = new UserController();

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
                .build();
        User resultUser = userController.create(testedUser);
        Assertions.assertEquals(expectedUser,resultUser);
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
                .build();

        User resultUser = userController.create(testedUser);
        Assertions.assertEquals(expectedUser,resultUser);
    }

}
