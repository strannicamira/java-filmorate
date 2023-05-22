package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;
import static ru.yandex.practicum.filmorate.Constants.ASCENDING_ORDER;


@Service
@Slf4j
public class InMemoryFilmService implements FilmService {

    private static final String sort = DESCENDING_ORDER;
    private final FilmStorage filmStorage;

    public InMemoryFilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

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
        return filmStorage.getFilms()
                .values()
                .stream()
                .sorted((p0, p1) -> compare(p0, p1, sort))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1, String sort) {
        int result = f0.getLikes().size() - (f1.getLikes().size());
        switch (sort) {
            case ASCENDING_ORDER:
                result = 1 * result;
                break;
            case DESCENDING_ORDER:
                result = -1 * result; //обратный порядок сортировки
                break;
        }
        return result;
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
