package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@Slf4j
public class FilmController {

    private ConcurrentHashMap<Integer, Film> films = new ConcurrentHashMap<>();
    private Integer idCounter = 0;

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    @ResponseBody
    public Film create(@Valid @RequestBody Film film) {
        if (film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
        ) {
            log.warn("Фильм не создан. Не прошел проверку: '{}'", film);
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
        if (film != null && films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Фильм обновлен: '{}'", film);
        } else {
            log.warn("Фильм не обновлен. Не найден в списке: '{}'", film);
            throw new ValidationException();
        }
        return film;
    }
}