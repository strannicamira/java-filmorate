package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDaoStorageImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        if (film.getDescription().length() > 200 ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))
        ) {
            throw new ValidationException("Фильм не создан. Не прошел проверку.");
        }
        Film resultFilm = Film.builder()
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(film.getGenres())
                .build();

        int id = insert(resultFilm);
        resultFilm.setId(id);
        insertMpa(resultFilm);
        insertGenres(resultFilm);

        log.info("Фильм создан: '{}'", resultFilm);
        return resultFilm;
    }

    private int insert(Film film) {
        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("films")
                        .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());

        log.info("Будет сохранен Фильм: '{}'", parameters);

        return simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
    }

    private int insertMpa(Film film) {

        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_mpa");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_id", film.getId());
        parameters.put("mpa_id", film.getMpa().getId());

        log.info("Будет сохранен mpa: '{}'", parameters);

        return simpleJdbcInsert.execute(parameters);
    }

    private void insertGenres(Film film) {

        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_genre");

        for (Genres genre : film.getGenres()){
            Map<String, Object> parameters = new HashMap<>();

            parameters.put("film_id", film.getId());
            parameters.put("genre_id", genre.getId());

            log.info("Будет сохранен genre: '{}'", parameters);

             simpleJdbcInsert.execute(parameters);
        }

    }

    @Override
    public Film update(Film film) {
        if (film == null) {
            throw new NotFoundException("Пользователь не будет обновлен. Никто.");
        }
        put(film);
        log.info("Пользователь обновлен: '{}'", film);
        return film;
    }

    private int put(Film film) {
        String sqlQuery = "update films set name = ?, description = ? , release_date = ?where id = ?";
        int count = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getId());
        if (count == 0) {
            throw new NotFoundException("Пользователь не сохранен: " + film.getName());
        }
        return count;
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT FILMS.id, FILMS.name, FILMS.description, FILMS.release_date, FILMS.duration, C.NAME AS GENRE, R.NAME AS MPA\n" +
                        "FROM FILMORATE.PUBLIC.FILMS AS FILMS\n" +
                        "LEFT JOIN FILM_GENRE FC on FILMS.ID = FC.FILM_ID\n" +
                        "    LEFT JOIN GENRES C on C.ID = FC.GENRE_ID\n" +
                        "LEFT JOIN FILM_MPA FR on FILMS.ID = FR.FILM_ID\n" +
                        "LEFT JOIN MPA R on R.ID = FR.MPA_ID\n" +
                        "WHERE FILMS.ID = ?", id);

        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
//                    .mpa(Mpa.valueOf("mpa"))
//                    .genres((Set<Genres>)filmRows.getObject("genres"))
                    .build();

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм не обновлен. Не может быть найден.");
//            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select id from films ");
        List<Film> films = Useful.getInt(userRows, "id").stream().map(id -> findFilmById(id).get()).collect(Collectors.toList());
        return films;
    }

}
