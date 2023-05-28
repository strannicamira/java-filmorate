package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class Useful {
    static Set<Integer> getInt(SqlRowSet userRows, String label) {
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

    static Set<Genres> getGenres(SqlRowSet userRows, String label) {
        Set<Genres> collection = new HashSet<>();
        userRows.first();
        do {
            if (userRows.getRow() != 0) {
                Genres genre = userRows.getObject(label, Genres.class);
                if (!userRows.wasNull()) {
                    collection.add(genre);
                }
            }
        } while (userRows.next());
        return collection;
    }


    static Set<Integer> getInt(ResultSet resultSet, String label) throws SQLException {
        Set<Integer> collection = new HashSet<>();
        log.info("Коллекция часть {}.", resultSet);

        collection.add(resultSet.getInt(label));
        log.info("Коллекция {}.", collection);

        return collection;
    }

    Collection<SqlRowSet> getSqlRowSet(SqlRowSet userRows) {
        Collection<SqlRowSet> collection = new HashSet<>();
        userRows.first();
        do {
            log.info("Коллекция часть {}.", userRows);

            collection.add(userRows);
        } while (userRows.next());
        log.info("Коллекция {}.", collection);

        return collection;
    }
}
