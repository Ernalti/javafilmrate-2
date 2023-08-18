package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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