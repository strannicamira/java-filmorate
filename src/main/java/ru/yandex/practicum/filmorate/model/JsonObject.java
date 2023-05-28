package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class JsonObject {
    private final int id;

    public JsonObject(int id) {
        this.id = id;
    }
}
