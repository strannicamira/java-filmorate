package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") Integer id){
        return filmStorage.findFilmById(id);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public boolean addLike(@PathVariable("id") Integer filmId,
                           @PathVariable("userId") Integer userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable("id") Integer filmId,
                              @PathVariable("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> findTop(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findTopLiked(count);
    }

}
