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
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select id from users ");
        List<User> users = collectInt(userRows, "id").stream().map(id -> findUserById(id).get()).collect(Collectors.toList());
        return users;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT DISTINCT U.ID, U.LOGIN, U.NAME, U.EMAIL, U.BIRTHDAY, FR.RESPONDER_ID AS FRIEND\n" +
                "FROM FILMORATE.PUBLIC.USERS AS U\n" +
                "LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FR ON U.ID = FR.REQUESTER_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers);
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
//                SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT ID, LOGIN,NAME,EMAIL,BIRTHDAY, FR.REQUESTER_ID AS FRIENDS\n" +
                "FROM FILMORATE.PUBLIC.USERS AS U\n" +
                "          LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FR ON U.ID = FR.RESPONDER_ID \n" +
                "WHERE U.ID = ?", id);

        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("id"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .email(userRows.getString("email"))
                    .birthday(userRows.getDate("birthday").toLocalDate())
//                    .friends((Set<Integer>) collect(userRows).stream().map(arg -> arg.getInt("FRIENDS")).collect(Collectors.toCollection(HashSet::new)))
                    .friends(collectInt(userRows, "FRIENDS"))
                    .build();

            log.info("Найден пользователь: {} {}", user.getId(), user.getLogin());

            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь не обновлен. Не может быть найден.");
//            return Optional.empty();
        }
    }

    private Set<Integer> collectInt(SqlRowSet userRows, String label) {
        Set<Integer> collection = new HashSet<>();
        userRows.first();
        do {
            if (userRows.getRow() != 0) {
                int i = userRows.getInt(label);
                if (!userRows.wasNull()) {
                    collection.add(i);
                }
            }
        } while (userRows.next());
        return collection;
    }

    private Set<Integer> convertInt(ResultSet resultSet, String label) throws SQLException {
        Set<Integer> collection = new HashSet<>();
        log.info("Коллекция часть {}.", resultSet);

        collection.add(resultSet.getInt(label));
        log.info("Коллекция {}.", collection);

        return collection;
    }


    Collection<SqlRowSet> collect(SqlRowSet userRows) {
        Collection<SqlRowSet> collection = new HashSet<>();
        userRows.first();
        do {
            log.info("Коллекция часть {}.", userRows);

            collection.add(userRows);
        } while (userRows.next());
        log.info("Коллекция {}.", collection);

        return collection;
    }

    private User mapRowToUsers(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(convertInt(resultSet, "FRIEND"))
                .build();
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {//1[4]: userId=1, friendId=4, friends:responder_id=1=userId, requester_id=4=friendId
        //        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from friends where requester_id = ? and responder_id = ?", friendId, userId);

        String sqlQueryUpdate = "update friends set is_friends = ? where responder_id = ? and requester_id = ? ";
        int count = jdbcTemplate.update(sqlQueryUpdate, true, friendId, userId);

        String sqlQueryInsert = "insert into friends(responder_id, requester_id, is_friends) values (?, ?, ?)";
        if (count == 0) {
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, false);
        } else {
            jdbcTemplate.update(sqlQueryInsert, userId, friendId, true);
        }
    }

    @Override
    public List<User> getFriends(Integer userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select FRIENDS.REQUESTER_ID AS ID from FILMORATE.PUBLIC.FRIENDS AS FRIENDS where FRIENDS.RESPONDER_ID = ? ", userId);
        List<User> users = collectInt(userRows, "ID").stream().map(id -> findUserById(id).get()).collect(Collectors.toList());
        return users;
    }

    //    @Override
    public List<User> getFriendsBySQL(Integer userId) {
        String sqlQuery = "SELECT DISTINCT USERS.ID, USERS.LOGIN, USERS.NAME, USERS.EMAIL, USERS.BIRTHDAY, FRIENDS.RESPONDER_ID AS FRIEND\n" +
                "FROM FILMORATE.PUBLIC.USERS AS USERS\n" +
                "LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FRIENDS ON USERS.ID = FRIENDS.REQUESTER_ID\n" +
                "LEFT JOIN FILMORATE.PUBLIC.USERS AS USERFRIENDS  on USERFRIENDS.ID = FRIENDS.RESPONDER_ID\n" +
                "LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FRIENDRIENDS ON FRIENDRIENDS.REQUESTER_ID = FRIENDS.RESPONDER_ID\n" +
                "WHERE USERS.ID=?";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery = "select FRIENDS.REQUESTER_ID AS ID from FILMORATE.PUBLIC.FRIENDS AS FRIENDS where FRIENDS.RESPONDER_ID = ? AND\n" +
                "    FRIENDS.REQUESTER_ID IN (select FRIENDS.REQUESTER_ID from FILMORATE.PUBLIC.FRIENDS AS FRIENDS where FRIENDS.RESPONDER_ID = ?)";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id, otherId);
        List<User> users = collectInt(userRows, "ID").stream().map(i -> findUserById(i).get()).collect(Collectors.toList());
        return users;
    }

    //    @Override
    public List<User> getCommonFriendsBySQL(Integer id, Integer otherId) {
        String sqlQuery = "SELECT DISTINCT USERS.ID, USERS.LOGIN, USERS.NAME, USERS.EMAIL, USERS.BIRTHDAY, FRIENDS.RESPONDER_ID AS FRIEND\n" +
                "FROM FILMORATE.PUBLIC.USERS AS USERS\n" +
                "LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FRIENDS ON USERS.ID = FRIENDS.REQUESTER_ID\n" +
                "WHERE USERS.ID IN (\n" +
                "SELECT DISTINCT FR.RESPONDER_ID AS FRIENDS\n" +
                "FROM FILMORATE.PUBLIC.USERS AS U\n" +
                "         LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FR ON U.ID = FR.REQUESTER_ID\n" +
                "WHERE U.ID=? AND FR.RESPONDER_ID IN (\n" +
                "    SELECT DISTINCT FR.RESPONDER_ID AS FRIENDS\n" +
                "    FROM FILMORATE.PUBLIC.USERS AS U\n" +
                "             LEFT JOIN FILMORATE.PUBLIC.FRIENDS AS FR ON U.ID = FR.REQUESTER_ID\n" +
                "    WHERE U.ID=?\n" +
                "    ))";
//        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers, id, otherId);
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {//4 [1]
        String sqlQueryUpdate = "update friends set is_friends = ? " +
                "where requester_id = ? and responder_id = ?";
        int count = jdbcTemplate.update(sqlQueryUpdate, false, userId, friendId);

        String sqlQuery = "delete from friends where REQUESTER_ID = ? and  RESPONDER_ID = ?";
        return jdbcTemplate.update(sqlQuery, friendId, userId) > 0;

    }


}
