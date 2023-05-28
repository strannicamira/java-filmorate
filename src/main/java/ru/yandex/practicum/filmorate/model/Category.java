package ru.yandex.practicum.filmorate.model;

public enum Category {
    COMEDY(1,"Комедия"),
    DRAMA(2,"Драма"),
    CARTOON(3,"Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5,"Документальный"),
    ACTION(6,"Боевик");

    private final int id;
    private final String name;

    Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
