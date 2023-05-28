package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Genres {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final Integer id;
    private final String name;

    Genres(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonValue
    public JsonObject getJsonId() {
        return new JsonObject(id);
    }

    public Integer getId() {
        return id;
    }
    @JsonCreator
    public static Genres forValues(@JsonProperty("id") Integer id) {
        for (Genres genre : Genres.values()) {
            if (genre.id == id) {
                return genre;
            }
        }
        return null;
    }

}
