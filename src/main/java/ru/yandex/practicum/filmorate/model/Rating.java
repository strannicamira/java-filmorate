package ru.yandex.practicum.filmorate.model;

public enum Rating {
    G(1,"G"),
    PG(2,"PG"),
    PG_13(3,"PG-13"),
    R(4, "R"),
    NC_17(5,"NC-17");

    private final int id;
    private final String name;

    Rating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
