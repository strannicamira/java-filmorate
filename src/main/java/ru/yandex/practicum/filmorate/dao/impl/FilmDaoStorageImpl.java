package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
@Slf4j
public class FilmDaoStorageImpl implements FilmDao {
    @Override
    public List<Film> findAll() {
        return null;
    }
}
