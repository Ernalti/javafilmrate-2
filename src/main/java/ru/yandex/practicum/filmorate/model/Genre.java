package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Genre {
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer id;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String name;

}
