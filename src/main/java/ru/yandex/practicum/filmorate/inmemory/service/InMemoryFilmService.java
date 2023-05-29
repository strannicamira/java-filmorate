package ru.yandex.practicum.filmorate.inmemory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.inmemory.storage.FilmStorage;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = findFilmById(filmId);
        if (film.getLikes().remove(userId)) {
            log.debug("Отметка удалена: '{}'", film);
            return film;
        } else {
            throw new NotFoundException("Отметку не удалось удалить.");
        }
    }

    @Override
    public List<Film> findTopLiked(Integer count) {
        return filmStorage.findTopLiked(count);
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public Film findFilmById(Integer id) {
        return filmStorage.findFilmById(id);
    }
}
