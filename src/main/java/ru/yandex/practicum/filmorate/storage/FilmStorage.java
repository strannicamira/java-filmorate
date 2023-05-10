package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

     Map<Integer, Film> getFilms();

     List<Film> findAll();

     Film create(Film film);

     Film update(Film film);

     Film findFilmById(Integer id);
}
