package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class GenreDaoImpl  implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    void insertGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<Genres>());
        }
        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_genre");
        for (Genres genre : film.getGenres()) {
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put("film_id", film.getId());
            parameters.put("genre_id", genre.getId());

            log.info("Будет сохранен genre: '{}'", parameters);

            simpleJdbcInsert.execute(parameters);
        }
    }

    void updateGenres(Film film) {
        deleteGenres(film);
        insertGenres(film);
    }

    int deleteGenres(Film film) {
        int countDelete = 0;
        Set<Genres> genres = film.getGenres();
        String sqlQueryDelete = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        countDelete = jdbcTemplate.update(sqlQueryDelete, film.getId());
        return countDelete;
    }

    @Override
    public Optional<Genres> findGenreByIdOptionally(Integer id) {
        String sql = "SELECT * FROM GENRES WHERE ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            Genres obj = Genres.forValues(rows.getInt("id"));

            log.info("Найден Genre: {} {}", obj.getId(), obj.getName());

            return Optional.of(obj);
        } else {
            log.info("Genre с идентификатором {} не найден.", id);
            throw new NotFoundException("Genre не найден.");
        }
    }

    @Override
    public List<Genres> findAllGenres() {
        String sql = "SELECT DISTINCT * FROM GENRES";
        List<Genres> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
        return list;
    }

    private Genres makeGenre(ResultSet rs) throws SQLException {
        return Genres.forValues(rs.getInt("ID"));
    }

    int compareGenres(Genres f0, Genres f1, String sort) {
        int result = f0.getId() - (f1.getId());
        switch (sort) {
            case Constants.ASCENDING_ORDER:
                result = 1 * result;
                break;
            case Constants.DESCENDING_ORDER:
                result = -1 * result;
                break;
        }
        return result;
    }
}