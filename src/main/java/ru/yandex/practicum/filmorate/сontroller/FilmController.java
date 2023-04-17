package ru.yandex.practicum.filmorate.сontroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {

    private HashMap<Integer, Film> films = new HashMap<>();
    private Integer idCounter = 0;

    @GetMapping("/films")
    public HashMap<Integer, Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    @ResponseBody
    public Film create(@Valid @RequestBody Film film) {
        if (film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
        ) {
            throw new ValidationException();
        }
        Film resultFilm = Film.builder()
                .id(++idCounter)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
        films.put(resultFilm.getId(), resultFilm);
        log.debug("Фильм создан: '{}'", resultFilm);
        return resultFilm;
    }

    @PutMapping(value = "/films")
    @ResponseBody
    public Film update(@Valid @RequestBody Film film) {
        films.put(film.getId(), film);
        log.debug("Фильм обновлен: '{}'", film);
        return film;
    }
}
