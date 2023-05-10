package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

public interface FilmService extends FilmStorage {
    boolean addLike(Integer filmId, Integer userId);

    boolean deleteLike(Integer filmId, Integer userId);

    List<Film> findTopLiked(Integer count);
}
