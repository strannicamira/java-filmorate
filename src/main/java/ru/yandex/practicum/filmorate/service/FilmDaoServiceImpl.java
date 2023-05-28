package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmDaoServiceImpl implements FilmDaoService{
    private final FilmDao filmDao;

    @Override
    public List<Film> findAll() {
        return filmDao.findAll();
    }
}
