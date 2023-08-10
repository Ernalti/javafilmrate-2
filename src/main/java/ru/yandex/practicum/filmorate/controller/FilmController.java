package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Обработка запроса на получение списка фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id) {
        log.info("Обработка запроса на получение фильма с id={}", id);
        return filmService.findFilm(id);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Обработка запроса на получение списка из {} популярных фильмов", count);
        return filmService.getPopFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Обработка запроса на создание фильма {}",film.getName());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обработка запроса на обновление фильма {}",film.getName());
        return filmService.update(film);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Обработка запроса на присвоение фильму {} лайка пользователем {}",id,userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Обработка запроса на удаление лайка фильму {} пользователем {}",id,userId);
        return filmService.delLike(id, userId);
    }


    @DeleteMapping
    public void clearAll() {
        log.info("Обработка на удаление всех фильмов");
        filmService.clearAll();
    }

}