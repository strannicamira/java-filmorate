package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findFilmById(Integer id);

    Mpa findMpaById(Integer id);

    Optional<Mpa> findMpaByIdOptionally(Integer id);

    List<Mpa> findAllMpa();

    Genres findGenreById(Integer id);

    Optional<Genres> findGenreByIdOptionally(Integer id);

    List<Genres> findAllGenres();

    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);

    List<Film> findTopLiked(Integer count);
}
