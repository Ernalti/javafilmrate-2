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
public class Film {
    private Integer id;
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @NotBlank
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Long duration;
    @JsonIgnore
    private Set<Integer> likes;

    public Film() {
        this.likes = new HashSet<>(); // Initialize the set in the constructor
    }

    public void addLike(Integer id) {
        this.likes.add(id);
    }

    public void delLike(Integer id) {
        this.likes.remove(id);
    }
}