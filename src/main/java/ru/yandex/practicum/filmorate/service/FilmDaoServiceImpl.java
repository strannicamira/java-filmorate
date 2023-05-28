package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmDaoServiceImpl implements FilmDaoService {
    private final FilmDao filmDao;

    @Override
    public List<Film> findAll() {
        return filmDao.findAll();
    }

    @Override
    public Film create(Film film) {
        return filmDao.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmDao.update(film);
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        return filmDao.findFilmById(id);
    }
}
