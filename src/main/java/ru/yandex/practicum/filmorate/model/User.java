package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;

}