package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/films/{id}")
    public Optional<Film> getFilm(@PathVariable("id") Integer id) {
        return filmService.findFilmById(id);
    }


    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }


    @GetMapping("/mpa/{id}")
    public Optional<Mpa> findMpaByIdOptionally(@PathVariable("id") Integer id) {
        return filmService.findMpaByIdOptionally(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> findAllMpa() {
        return filmService.findAllMpa();
    }


    @GetMapping("/genres/{id}")
    public Optional<Genres> findGenreByIdOptionally(@PathVariable("id") Integer id) {
        return filmService.findGenreByIdOptionally(id);
    }

    @GetMapping("/genres")
    public List<Genres> findAllGenres() {
        return filmService.findAllGenres();
    }


    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Integer filmId,
                        @PathVariable("userId") Integer userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") Integer filmId,
                           @PathVariable("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> findTop(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findTopLiked(count);
    }
}
