package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres/{id}")
    public Optional<Genres> findGenreByIdOptionally(@PathVariable("id") Integer id) {
        return genreService.findGenreByIdOptionally(id);
    }

    @GetMapping("/genres")
    public List<Genres> findAllGenres() {
        return genreService.findAllGenres();
    }
}