package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Mpa {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final Integer id;
    private final String name;

    Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

//    @JsonValue
//    public JsonObject getJsonId() {
//        return new JsonObject(id);
//    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static Mpa forValues(@JsonProperty("id") Integer id) {
        for (Mpa mpa : Mpa.values()) {
            if (mpa.id == id) {
                return mpa;
            }
        }
        return null;
    }


}
