package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;
    @NotEmpty
    private String name;
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Integer> likes;
    private Set<Genres> genres;
    private Mpa mpa;

    public Set<Integer> getLikes() {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes;
    }

}
