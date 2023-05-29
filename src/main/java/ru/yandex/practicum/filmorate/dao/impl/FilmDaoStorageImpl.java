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
    }

    private void updateFilmMpa(Film film) {
        rowDeleteMpa(film);
        insertMpa(film);
    }

    private int rowInsertMpa(Film film) {
        String sqlQueryInsert = "insert into film_genre(film_id, mpa_id) values (?, ?)";
        Integer filmId = film.getId();
        Integer mpaId = film.getMpa().getId();
        int count = jdbcTemplate.update(sqlQueryInsert, filmId, mpaId);
        if (count == 0) {
            throw new NotFoundException("mpa не удалось обновить: film_id/mpa_id" + filmId.toString() + "/" + mpaId);
        }
        return count;
    }

/*
    private int rowUpdateMpa(Film film) {
        int count;
        String sqlQuery = "update film_mpa set film_id = ?, mpa_id = ? where film_id = ? and mpa_id = ?";
        Integer filmId = film.getId();
        Integer mpaId = film.getMpa().getId();
        count = jdbcTemplate.update(sqlQuery, filmId, mpaId, filmId, mpaId);
        return count;
    }
*/


    private int rowDeleteMpa(Film film) {
        String sqlQueryDelete = "delete from film_mpa where film_id = ?";
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
        if (genres != null && genres.size() != 0) {
            String sqlQueryDelete = "delete from film_genre where film_id = ?";
            countDelete = jdbcTemplate.update(sqlQueryDelete, film.getId());
//            if (countDelete == 0) {
//                throw new NotFoundException("genre не удалось обновить: film_id " + film.getId());
//            }
        }
        return countDelete;
    }

    private void rowUpdateGenres(Film film) {
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
                        "WHERE FILMS.ID = ? " +
                        "ORDER BY GENRE", id);


        if (filmRows.next()) {
            filmRows.first();
            Film film = Film.builder()
                    .id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .mpa(Mpa.forValues(filmRows.getInt("MPA")))
                    .genres(Useful.getInt(filmRows, "GENRE").stream().map(genreId -> Genres.forValues(genreId)).sorted((p0, p1) -> compareGenres(p0, p1, sort)).collect(Collectors.toCollection(TreeSet::new)))
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


    @Override
    public Mpa findMpaById(Integer id) {
        String sql = "select * from mpa where id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id);
        return mpa.get(0);
    }

    @Override
    public Optional<Mpa> findMpaByIdOptionally(Integer id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from mpa where id = ?", id);
        if (rows.next()) {
            Mpa mpa = Mpa.forValues(rows.getInt("id"));

            log.info("Найден пользователь: {} {}", mpa.getId(), mpa.getName());

            return Optional.of(mpa);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findAllMpa() {
        String sql = "select distinct * from mpa";
        List<Mpa> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
        return mpa;
    }


    @Override
    public Genres findGenreById(Integer id) {
        String sql = "select * from genres where id = ?";
        List<Genres> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        return list.get(0);
    }

    @Override
    public Optional<Genres> findGenreByIdOptionally(Integer id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from genres where id = ?", id);
        if (rows.next()) {
            Genres obj = Genres.forValues(rows.getInt("id"));

            log.info("Найден пользователь: {} {}", obj.getId(), obj.getName());

            return Optional.of(obj);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Genres> findAllGenres() {
        String sql = "select distinct * from genres";
        List<Genres> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
        return list;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.forValues(rs.getInt("id"));
    }

    private Genres makeGenre(ResultSet rs) throws SQLException {
        return Genres.forValues(rs.getInt("id"));
    }

    private static final String sort = ASCENDING_ORDER;

    private int compareGenres(Genres f0, Genres f1, String sort) {
        int result = f0.getId() - (f1.getId());
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


    /////////////////////////////////////////
    @Override
    public Film addLike(Integer filmId, Integer userId) {
        insertLike(filmId, userId);
        return findFilmById(filmId).get();
    }

    private void insertLike(Integer filmId, Integer userId) {

        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_like");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("film_id", filmId);
        parameters.put("user_id", userId);

        log.info("Будет сохранен mpa: '{}'", parameters);
        simpleJdbcInsert.execute(parameters);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        rowDeleteLike(filmId, userId);
        return findFilmById(filmId).get();
    }

    private int rowDeleteLike(Integer filmId, Integer userId) {
        String sqlQueryDelete = "delete from film_like where film_id = ? and user_id =?";
        int count = jdbcTemplate.update(sqlQueryDelete, filmId, userId);
        if (count == 0) {
            throw new NotFoundException("Не удалось лайкнуть: film_id/user_id " + filmId + "/" + userId);
        }
        return count;
    }


    @Override
    public List<Film> findTopLiked(Integer count) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT FILMS.ID AS ID\n" +
                "FROM FILMORATE.PUBLIC.FILMS AS FILMS\n" +
                "         LEFT JOIN FILM_LIKE FL on FILMS.ID = FL.FILM_ID\n" +
                "GROUP BY FILMS.ID\n" +
                "ORDER BY COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?", count);
        return Useful.getInt(rows, "ID").stream().map(id->findFilmById(id).get()).collect(Collectors.toList());


//        return filter(count, sort);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.forValues(rs.getInt("MPA")))
                .genres(Useful.getInt(rs, "GENRE").stream().map(genreId -> Genres.forValues(genreId)).sorted((p0, p1) -> compareGenres(p0, p1, sort)).collect(Collectors.toCollection(TreeSet::new)))
                .likes(Useful.getInt(rs, "LIKE_FROM_USER"))
                .build();
    }

/*    public List<Film> filter(Integer count, String sort) {
        return films
                .values()
                .stream()
                .sorted((p0, p1) -> compare(p0, p1, sort))
                .limit(count)
                .collect(Collectors.toList());
    }*/

    private int compareFilms(Film f0, Film f1, String sort) {
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
////////////////////////////////////////////////////////////////
}
