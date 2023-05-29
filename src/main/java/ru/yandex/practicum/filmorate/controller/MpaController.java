package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/mpa/{id}")
    public Optional<Mpa> findMpaByIdOptionally(@PathVariable("id") Integer id) {
        return mpaService.findMpaByIdOptionally(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> findAllMpa() {
        return mpaService.findAllMpa();
    }
}