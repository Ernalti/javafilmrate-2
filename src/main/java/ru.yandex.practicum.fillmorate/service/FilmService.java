package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {
    List<Film> findAll();

    Film findFilm(Integer id);

    List<Film> getPopFilms(Integer count);

    Film create(Film film);

    Film update(Film film);

    Film addLike(Integer id, Integer userId);

    Film delLike(Integer id, Integer userId);

    void clearAll();

}