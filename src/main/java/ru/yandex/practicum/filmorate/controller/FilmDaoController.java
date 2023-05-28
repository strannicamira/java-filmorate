package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDaoService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmDaoController {
    private final FilmDaoService filmDaoService;

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmDaoService.findAll();
    }

}
