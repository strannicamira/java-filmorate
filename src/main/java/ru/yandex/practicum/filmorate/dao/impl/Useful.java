package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
