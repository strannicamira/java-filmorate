package ru.yandex.practicum.filmorate.сontroller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.HashMap;

@RestController
public class FilmController {

    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public HashMap<Integer, Film> findAll() {
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())||
                film.getDescription().length()>200  ||
                film.getReleaseDate().isBefore(LocalDate.of(1985,12,28))) {
            throw new ValidationException();
        }
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
