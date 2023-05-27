package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

        if(!findAll().stream().anyMatch(u -> u.getId() == user.getId())){
            throw new NotFoundException("Пользователь не обновлен. Не может быть найден.");
        }

        String sqlQuery = "update users set login = ?, name = ?, email = ?, birthday = ? " +
                "where id = ?";
//        try {
            jdbcTemplate.update(sqlQuery
                    , user.getLogin()
                    , user.getName()
                    , user.getEmail()
                    , user.getBirthday()
                    , user.getId());
//        } catch (Exception e){
//            throw new NotFoundException("Пользователь не обновлен:" + e.getMessage());
//        }


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
}
