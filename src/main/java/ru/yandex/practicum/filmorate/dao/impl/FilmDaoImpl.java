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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.Constants.ASCENDING_ORDER;
import static ru.yandex.practicum.filmorate.Constants.DESCENDING_ORDER;

@Component
@Slf4j
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
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
        insertMpa(resultFilm);
        insertGenres(resultFilm);

        log.info("Фильм создан: '{}'", resultFilm);
        return findFilmById(id).get();
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

    private void insertMpa(Film film) {

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

    private void insertGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        }
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
        updateFilmMpa(film);
        updateGenres(film);

        log.info("Пользователь обновлен: '{}'", film);
        return findFilmById(film.getId()).get();
    }

    private void updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION = ? WHERE ID = ?";
        int count = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDescription(),
                film.getDuration(),
                film.getId());

        if (count == 0) {
            throw new NotFoundException("Пользователь не сохранен: " + film.getName());
        }
    }

    private void updateFilmMpa(Film film) {
        rowDeleteMpa(film);
        insertMpa(film);
    }

    private int rowDeleteMpa(Film film) {
        String sqlQueryDelete = "DELETE FROM FILM_MPA WHERE FILM_ID = ?";
        int count = jdbcTemplate.update(sqlQueryDelete, film.getId());
        if (count == 0) {
            throw new NotFoundException("mpa не удалось удалить при обновить: film_id " + film.getId());
        }
        return count;
    }

    private void updateGenres(Film film) {
        deleteGenres(film);
        insertGenres(film);
    }

    private int deleteGenres(Film film) {
        int countDelete = 0;
        Set<Genres> genres = film.getGenres();
        String sqlQueryDelete = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        countDelete = jdbcTemplate.update(sqlQueryDelete, film.getId());
        return countDelete;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT ID FROM FILMS";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
        List<Film> films = SqlRowResultParser.getIntSet(rows, "id").stream().map(id -> findFilmById(id).get()).collect(Collectors.toList());
        return films;
    }

    @Override
    public Optional<Mpa> findMpaByIdOptionally(Integer id) {

        String sql = "SELECT * FROM MPA WHERE ID = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        if (rows.next()) {
            Mpa mpa = Mpa.forValues(rows.getInt("id"));

            log.info("Найден mpa: {} {}", mpa.getId(), mpa.getName());

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

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.forValues(rs.getInt("ID"));
    }

    private Genres makeGenre(ResultSet rs) throws SQLException {
        return Genres.forValues(rs.getInt("ID"));
    }

    private static final String sort = ASCENDING_ORDER;

    private int compareGenres(Genres f0, Genres f1, String sort) {
        int result = f0.getId() - (f1.getId());
        switch (sort) {
            case ASCENDING_ORDER:
                result = 1 * result;
                break;
            case DESCENDING_ORDER:
                result = -1 * result;
                break;
        }
        return result;
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        insertLike(filmId, userId);
        return findFilmById(filmId).get();
    }

    private void insertLike(Integer filmId, Integer userId) {

        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("FILM_LIKE");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FILM_ID", filmId);
        parameters.put("USER_ID", userId);

        log.info("Будет сохранен mpa: '{}'", parameters);
        simpleJdbcInsert.execute(parameters);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        rowDeleteLike(filmId, userId);
        return findFilmById(filmId).get();
    }

    private int rowDeleteLike(Integer filmId, Integer userId) {
        String sqlQueryDelete = "DELETE FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID =?";
        int count = jdbcTemplate.update(sqlQueryDelete, filmId, userId);
        if (count == 0) {
            throw new NotFoundException("Не удалось лайкнуть: film_id/user_id " + filmId + "/" + userId);
        }
        return count;
    }

    @Override
    public List<Film> findTopLiked(Integer count) {
        String sqlQuery = "SELECT FILMS.ID AS ID\n" +
                "FROM FILMORATE.PUBLIC.FILMS AS FILMS\n" +
                "         LEFT JOIN FILM_LIKE FL on FILMS.ID = FL.FILM_ID\n" +
                "GROUP BY FILMS.ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlQuery, count);
        return SqlRowResultParser.getIntSet(rows, "ID").stream().map(id -> findFilmById(id).get()).collect(Collectors.toList());
    }


    @Override
    public Optional<Film> findFilmById(Integer id) {
        String sqlQuery = "SELECT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASE_DATE, FILMS.DURATION,  FC.GENRE_ID AS GENRE, FR.MPA_ID AS MPA, FL.USER_ID AS LIKE_FROM_USER\n" +
                "FROM FILMORATE.PUBLIC.FILMS AS FILMS\n" +
                "LEFT JOIN FILM_GENRE FC ON FILMS.ID = FC.FILM_ID\n" +
                "LEFT JOIN FILM_MPA FR ON FILMS.ID = FR.FILM_ID\n" +
                "LEFT JOIN FILM_LIKE FL ON FILMS.ID = FL.FILM_ID\n" +
                "WHERE FILMS.id=?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (rows.next()) {
            rows.first();
            Film film = makeFilm(rows);

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм не обновлен. Не может быть найден.");
        }
    }

    private Film makeFilm(SqlRowSet rs) {
        return Film.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(Mpa.forValues(rs.getInt("MPA")))
                .genres(SqlRowResultParser.getIntSet(rs, "GENRE").stream().map(genreId -> Genres.forValues(genreId)).sorted((p0, p1) -> compareGenres(p0, p1, sort)).collect(Collectors.toCollection(TreeSet::new)))
                .likes(SqlRowResultParser.getIntSet(rs, "LIKE_FROM_USER"))
                .build();
    }
}
