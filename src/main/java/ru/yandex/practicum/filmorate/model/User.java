package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Integer id;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @NotBlank
    @Email
    private String email;
    @PastOrPresent
    private LocalDate birthday;
    @JsonIgnore
    private Set<Integer> friends;

    public User() {
        this.friends = new HashSet<>(); // Initialize the set in the constructor
    }

    public void addFriend(Integer id) {
        this.friends.add(id);
    }

    public void delFriend(Integer id) {
        this.friends.remove(id);
    }

}