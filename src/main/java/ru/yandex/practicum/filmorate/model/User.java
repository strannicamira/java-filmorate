package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
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
    //@Builder.Default
    private Set<Integer> friends;

    public Set<Integer> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
            return friends;
        } else {
            return friends;
        }
    }
/*    public void setFriends(Set<Integer> friends){
        if (friends == null) {
            this.friends = new HashSet<>();
        } else {
            this.friends = friends;
        }
    }*/
}
