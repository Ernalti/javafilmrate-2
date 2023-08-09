package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> findAll() {
        log.info("Обработка запроса на получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (beforeFirstFilm(film.getReleaseDate())) {
            log.warn("Ошибка создания фильма. дата релиза фильма не может быть раньше 28 декабря 1895 года. Фильм: {}",film);
            throw new ValidationException("дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        film.setId(nextId());
        films.put(film.getId(),film);
        log.info("Фильм успешно загружен. Фильм: {}",film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления фильма. Идентификатор не найден. Фильм: {}",film);
            throw new NotFoundException("Не найден фильм с id = " + film.getId());
        }
        if (beforeFirstFilm(film.getReleaseDate())) {
            log.warn("Ошибка обновления фильма. дата релиза фильма не может быть раньше 28 декабря 1895 года. Фильм: {}",film);
            throw new ValidationException("дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        films.replace(film.getId(),film);
        log.info("Обновление прошло успешно. Фильм: {}",film);
        return film;
    }

    @DeleteMapping
    public void clearAll() {
        log.info("Обработка запроса на удаление всех фильмов");
        films.clear();
    }

    private Integer nextId() {
        id++;
        log.debug("Изменение id фильмов: {}",id);
        return id;
    }

    private boolean beforeFirstFilm(LocalDate date) {
        return date.isBefore(LocalDate.of(1895, 12, 28));
    }
}
