package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Slf4j
public class UserDaoStorageImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToEmployee);
    }

    @Override
    public void create(User user) {
        String sqlQuery = "insert into users(login, name, email, birthday ) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
    }

    @Override
    public void update(User user) {
        String sqlQuery = "update users set login = ?, name = ?, email = ?, birthday = ? " +
                "where id = ?";
        int count = jdbcTemplate.update(sqlQuery
                , user.getLogin()
                , user.getName()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());
        if (count == 0) {
            throw new NotFoundException("Пользователь не обновлен. Не может быть найден.");
        }
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        log.info(" friends: {}","xnj");
//                SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT ID, LOGIN,NAME,EMAIL,BIRTHDAY, FR.RESPONDER_ID AS FRIENDS\n" +
                "FROM FILMORATE.PUBLIC.USERS AS U\n" +
                "         JOIN FILMORATE.PUBLIC.FRIENDS AS FR ON U.ID = FR.REQUESTER_ID\n" +
                "WHERE U.ID = ?", id);
        log.info(" friends: {}","xnfsdfsj");

        //log.info(" friends: {}", Stream.of(userRowsFriends).map(row -> row.getInt("RESPONDER_ID")).count());
        // обрабатываем результат выполнения запроса
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("id"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .email(userRows.getString("email"))
                    .birthday(userRows.getDate("birthday").toLocalDate())
                    .friends((Set<Integer>) Stream.of(userRows).map(row -> row.getInt("FRIENDS")).collect(Collectors.toCollection(HashSet::new)))
                    .build();

            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());

            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь не обновлен. Не может быть найден.");
//            return Optional.empty();
        }
    }

    private User mapRowToEmployee(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQueryInsert = "insert into friends(responder_id, requester_id, is_friends) values (?, ?, ?)";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friends where requester_id = ? and responder_id = ?", friendId, userId);
        String sqlQueryUpdate = "update friends set is_friends = ? " +
                "where requester_id = ? and responder_id = ?";
        int count = jdbcTemplate.update(sqlQueryUpdate, true, friendId, userId);
        if (count == 0) {
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, false);
        } else {
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, true);
        }
    }
}
