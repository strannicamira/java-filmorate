package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.InvalidIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserDaoStorageImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    public UserDaoStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT ID FROM USERS";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery);
        List<User> users = SqlRowResultParser.getIntSet(userRows, "ID").stream().map(id -> findUserById(id).get()).collect(Collectors.toList());
        return users;
    }

    @Override
    public User create(User user) {
        String userName = user.getName();
        if (!StringUtils.hasText(userName)) {
            userName = user.getLogin();
        }

        User resultUser = User.builder()
                .email(user.getEmail())
                .name(userName)
                .login(user.getLogin())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
        log.info("Будет создан пользователь: '{}'", resultUser);
        int id = insert(resultUser);
        resultUser.setId(id);
        log.info("Пользователь создан: '{}'", resultUser);
        return resultUser;
    }

    private void save(User user) {
        String sqlQuery = "INSERT INTO USERS(LOGIN, NAME, EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
    }

    private int insert(User user) {
        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("USERS")
                        .usingGeneratedKeyColumns("ID");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("LOGIN", user.getLogin());
        parameters.put("NAME", user.getName());
        parameters.put("EMAIL", user.getEmail());
        parameters.put("BIRTHDAY", user.getBirthday());

        log.info("Сохранение пользователя: '{}'", parameters);

        return simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
    }

    @Override
    public User update(User user) {
        updateUser(user);
        log.info("Пользователь обновлен: '{}'", user);
        return user;
    }

    private int updateUser(User user) {
        String sqlQuery = "UPDATE USERS SET LOGIN = ?, NAME = ?, EMAIL = ?, BIRTHDAY = ? WHERE ID = ?";
        int count = jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        if (count == 0) {
            throw new NotFoundException("Пользователь не сохранен: id/login" + +user.getId() + "/" + user.getLogin());
        }
        return count;
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        String sqlQuery = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY, FR.REQUESTER_ID AS FRIENDS\n" +
                "FROM FILMORATE.PUBLIC.USERS AS U\n" +
                "LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FR ON U.ID = FR.RESPONDER_ID \n" +
                "WHERE U.ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("ID"))
                    .login(userRows.getString("LOGIN"))
                    .name(userRows.getString("NAME"))
                    .email(userRows.getString("EMAIL"))
                    .birthday(userRows.getDate("BIRTHDAY").toLocalDate())
//                    .friends((Set<Integer>) collect(userRows).stream().map(arg -> arg.getInt("FRIENDS")).collect(Collectors.toCollection(HashSet::new)))
                    .friends(SqlRowResultParser.getIntSet(userRows, "FRIENDS"))
                    .build();

            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());

            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь не обновлен. Не может быть найден.");
//            return Optional.empty();
        }
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) { //1[4]: userId=1, friendId=4, friends:responder_id=1=userId, requester_id=4=friendId
        if (userId < 1 || friendId < 1) {
            throw new InvalidIdException("Пользователь не обновлен. Не может быть найден.");
        }
        String sqlQueryUpdate = "UPDATE FRIENDS SET IS_FRIENDS = ? WHERE RESPONDER_ID = ? AND REQUESTER_ID = ?";
        int count = jdbcTemplate.update(sqlQueryUpdate, true, friendId, userId);

        String sqlQueryInsert = "INSERT INTO FRIENDS(RESPONDER_ID, REQUESTER_ID, IS_FRIENDS) VALUES (?, ?, ?)";
        if (count == 0) {
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, false);
        } else {
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, true);
        }
    }

    @Override
    public List<User> getFriends(Integer userId) {
        String sqlQuery = "SELECT FRIENDS.REQUESTER_ID AS ID FROM FILMORATE.PUBLIC.FRIENDS AS FRIENDS WHERE FRIENDS.RESPONDER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        List<User> users = SqlRowResultParser.getIntSet(userRows, "ID").stream().map(id -> findUserById(id).get()).collect(Collectors.toList());
        return users;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery = "SELECT FRIENDS.REQUESTER_ID AS ID FROM FILMORATE.PUBLIC.FRIENDS AS FRIENDS WHERE FRIENDS.RESPONDER_ID = ? AND\n" +
                "FRIENDS.REQUESTER_ID IN (SELECT FRIENDS.REQUESTER_ID FROM FILMORATE.PUBLIC.FRIENDS AS FRIENDS WHERE FRIENDS.RESPONDER_ID = ?)";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id, otherId);
        List<User> users = SqlRowResultParser.getIntSet(userRows, "ID").stream().map(i -> findUserById(i).get()).collect(Collectors.toList());
        return users;
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) { //4 [1]
        String sqlQueryUpdate = "UPDATE FRIENDS SET IS_FRIENDS = ? WHERE REQUESTER_ID = ? AND RESPONDER_ID = ?";
        int count = jdbcTemplate.update(sqlQueryUpdate, false, userId, friendId);

        String sqlQueryDelete = "DELETE FROM FRIENDS WHERE REQUESTER_ID = ? AND  RESPONDER_ID = ?";
        return jdbcTemplate.update(sqlQueryDelete, friendId, userId) > 0;
    }

    private User mapRowToUsers(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("ID"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .friends(SqlRowResultParser.getIntSet(resultSet, "FRIEND"))
                .build();
    }

}
