package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest
class FilmControllerTests {
    FilmStorage filmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(filmStorage);
    FilmController filmController = new FilmController(filmStorage, filmService);

    @Test
    void create() {
        Film testedFilm = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(200)
                .build();

        Film expectedFilm = Film.builder()
                .id(1)
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(200)
                .likes(Collections.emptySet())
                .build();

        Film resultFilm = filmController.create(testedFilm);
        Assertions.assertEquals(expectedFilm, resultFilm);
    }


    @Test
    void createFailDescription() {
        Film testedFilm = Film.builder()
                .name("Film name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать " +
                        "господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                        "который за время «своего отсутствия», стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(1900, 3, 25))
                .duration(200)
                .build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmController.create(testedFilm);
                    }
                }
        );
    }

    @Test
    void createFailReleaseDate() {
        Film testedFilm = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1890, 3, 25))
                .duration(200)
                .build();

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        filmController.create(testedFilm);
                    }
                }
        );
    }
}
