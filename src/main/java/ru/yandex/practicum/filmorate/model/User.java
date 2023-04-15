package ru.yandex.practicum.filmorate.model;

import lombok.Data;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
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
}
