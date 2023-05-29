package ru.yandex.practicum.filmorate.inmemory.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.inmemory.storage.FilmStorage;

import java.util.List;

public interface FilmService extends FilmStorage {
    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);

    List<Film> findTopLiked(Integer count);
}
