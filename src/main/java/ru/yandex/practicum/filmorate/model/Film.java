package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres;


    public Film() {
        this.likes = new HashSet<>();
    }

    public void addLike(Integer id) {
        this.likes.add(id);
    }

    public void delLike(Integer id) {
        this.likes.remove(id);
    }
}