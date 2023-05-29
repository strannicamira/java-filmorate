package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void insertMpa(Film film) {

        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_mpa");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_id", film.getId());
        if (film.getMpa() != null) {
            parameters.put("mpa_id", film.getMpa().getId());
        } else {
            parameters.put("mpa_id", null);
        }

        log.info("Будет сохранен mpa: '{}'", parameters);
        simpleJdbcInsert.execute(parameters);
    }

    void updateFilmMpa(Film film) {
        rowDeleteMpa(film);
        insertMpa(film);
    }

    int rowDeleteMpa(Film film) {
        String sqlQueryDelete = "DELETE FROM FILM_MPA WHERE FILM_ID = ?";
        int count = jdbcTemplate.update(sqlQueryDelete, film.getId());
        if (count == 0) {
            throw new NotFoundException("mpa не удалось удалить при обновить: film_id " + film.getId());
        }
        return count;
    }

    @Override
    public Optional<Mpa> findMpaByIdOptionally(Integer id) {

        String sql = "SELECT * FROM MPA WHERE ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            Mpa mpa = Mpa.forValues(rows.getInt("id"));
            log.info("Найден mpa: {}", id);
            return Optional.of(mpa);
        } else {
            log.info("mpa с идентификатором {} не найден.", id);
            throw new NotFoundException("mpa не найден.");
        }
    }

    @Override
    public List<Mpa> findAllMpa() {
        String sql = "SELECT DISTINCT * FROM MPA";
        List<Mpa> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
        return mpa;
    }

    Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.forValues(rs.getInt("ID"));
    }
}