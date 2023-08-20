package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Long duration;

    @NotNull
    private Mpa mpa;

//  Понимаю, что так не правильно. пробовал через  @Builder.Default сделать, но всё равно создаётся изначально тип HashSet
    private LinkedHashSet<Genre> genres;
//    @Builder.Default
//      private Set<Genre> genres;  = new LinkedHashSet<>();


}