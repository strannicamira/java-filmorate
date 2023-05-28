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
import ru.yandex.practicum.filmorate.model.Mpa;

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

        int id = insertFilm(resultFilm);
        resultFilm.setId(id);
        if (resultFilm.getMpa() != null) {
            insertMpa(resultFilm);
        }
        if (resultFilm.getGenres() != null) {
            insertGenres(resultFilm);
        } else {
            resultFilm.setGenres(new HashSet<>());
        }

        log.info("Фильм создан: '{}'", resultFilm);
        return resultFilm;
    }

    private int insertFilm(Film film) {
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

        for (Genres genre : film.getGenres()) {
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
        updateFilm(film);
        log.info("Пользователь обновлен: '{}'", film);
        return film;
    }

    private int updateFilm(Film film) {
        String sqlQuery = "update films set name = ?, release_date = ?, description = ?, duration = ? where id = ?";
        int count = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getId());
        if (count == 0) {
            throw new NotFoundException("Пользователь не сохранен: " + film.getName());
        }
        if (film.getMpa() != null) {
            updateMpa(film);
        }
        if (film.getGenres() != null) {
            updateGenres(film);
        }
        return count;
    }

    private int updateMpa(Film film) {
        String sqlQuery = "update film_mpa set film_id = ?, mpa_id = ? where film_id = ? and mpa_id = ?";
        Integer filmId = film.getId();
        Integer mpaId = film.getMpa().getId();
        int count = jdbcTemplate.update(sqlQuery,
                filmId,
                mpaId,
                filmId,
                mpaId);
        if (count == 0) {

            String sqlQueryInsert = "insert into film_genre(film_id, genre_id) values (?, ?)";
            int countInsert = jdbcTemplate.update(sqlQueryInsert, filmId, mpaId);
            if (countInsert == 0) {
                throw new NotFoundException("mpa не удалось обновить: film_id/mpa_id" + filmId.toString() + "/" + mpaId);
            }
        }
        return count;
    }

    private void updateGenres(Film film) {
        String sqlQueryUpdate = "update film_genre set film_id = ?, genre_id = ? where film_id = ? and genre_id = ?";

        for (Genres genre : film.getGenres()) {
            Integer filmId = film.getId();
            Integer genreId = genre.getId();
            int countUpdate = jdbcTemplate.update(sqlQueryUpdate,
                    filmId,
                    genreId,
                    filmId,
                    genreId);
            if (countUpdate == 0) {
                String sqlQueryInsert = "insert into film_genre(film_id, genre_id) values (?, ?)";
                int countInsert = jdbcTemplate.update(sqlQueryInsert, filmId, genreId);
                if (countInsert == 0) {
                    throw new NotFoundException("genre не удалось обновить: film_id/genre_id " + film.getName() + "/" + genreId);

                }
            }
        }
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT FILMS.id, FILMS.name, FILMS.description, FILMS.release_date, FILMS.duration,  FC.GENRE_ID AS GENRE, FR.MPA_ID AS MPA\n" +
                        "FROM FILMORATE.PUBLIC.FILMS AS FILMS\n" +
                        "         LEFT JOIN FILM_GENRE FC on FILMS.ID = FC.FILM_ID\n" +
                        "         LEFT JOIN FILM_MPA FR on FILMS.ID = FR.FILM_ID\n" +
                        "WHERE FILMS.ID = ?", id);

        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(Mpa.forValues(filmRows.getInt("MPA")))
                    .genres(Useful.getInt(filmRows,"GENRE").stream().map(genreId -> Genres.forValues(genreId)).collect(Collectors.toCollection(HashSet::new)))
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
