package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDaoService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmDaoController {
    private final FilmDaoService filmDaoService;

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmDaoService.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmDaoService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmDaoService.update(film);
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilm(@PathVariable("id") Integer id) {
        return filmDaoService.findFilmById(id);
    }

}
