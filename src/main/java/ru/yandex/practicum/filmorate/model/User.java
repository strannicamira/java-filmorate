package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;


import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder
public class User {
    private Integer id;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "[^\\s]+")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends;
}
