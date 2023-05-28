package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findFilmById(Integer id);
}
