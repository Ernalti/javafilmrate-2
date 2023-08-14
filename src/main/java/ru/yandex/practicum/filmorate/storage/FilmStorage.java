package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(Film film, Integer userId);

    Film delLike(Film film, Integer userId);

    void clearFilms();

    Film getFilmById(Integer id);

}