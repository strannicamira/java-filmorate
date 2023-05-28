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
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select id from users ");
        List<User> users = Useful.getInt(userRows, "id").stream().map(id -> findUserById(id).get()).collect(Collectors.toList());
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
        String sqlQuery = "insert into users(login, name, email, birthday ) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday());
    }

    private int insert(User user) {
        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("users")
                        .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("login", user.getLogin());
        parameters.put("name", user.getName());
        parameters.put("email", user.getEmail());
        parameters.put("birthday", user.getBirthday());
        log.info("Будет сохранен пользователь: '{}'", parameters);

        return simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new NotFoundException("Пользователь не будет обновлен. Никто.");
        }
        put(user);
        log.info("Пользователь обновлен: '{}'", user);
        return user;
    }

    private int put(User user) {
        String sqlQuery = "update users set login = ?, name = ?, email = ?, birthday = ? where id = ?";
        int count = jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        if (count == 0) {
            throw new NotFoundException("Пользователь не сохранен: " + user.getLogin());
        }
        return count;
    }

    @Override
    public Optional<User> findUserById(Integer id) {
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
                    .friends(Useful.getInt(userRows, "FRIENDS"))
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
        List<User> users = Useful.getInt(userRows, "ID").stream().map(id -> findUserById(id).get()).collect(Collectors.toList());
        return users;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        String sqlQuery = "select FRIENDS.REQUESTER_ID AS ID from FILMORATE.PUBLIC.FRIENDS AS FRIENDS where FRIENDS.RESPONDER_ID = ? AND\n" +
                "    FRIENDS.REQUESTER_ID IN (select FRIENDS.REQUESTER_ID from FILMORATE.PUBLIC.FRIENDS AS FRIENDS where FRIENDS.RESPONDER_ID = ?)";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id, otherId);
        List<User> users = Useful.getInt(userRows, "ID").stream().map(i -> findUserById(i).get()).collect(Collectors.toList());
        return users;
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) { //4 [1]
        String sqlQueryUpdate = "update friends set is_friends = ? " +
                "where requester_id = ? and responder_id = ?";
        int count = jdbcTemplate.update(sqlQueryUpdate, false, userId, friendId);

        String sqlQuery = "delete from friends where REQUESTER_ID = ? and  RESPONDER_ID = ?";
        return jdbcTemplate.update(sqlQuery, friendId, userId) > 0;
    }

    private User mapRowToUsers(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .friends(Useful.getInt(resultSet, "FRIEND"))
                .build();
    }

}
