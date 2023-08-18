package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    Set<Genre> getGenreByFilm(Film film);

    List<Genre> getAllGenre();

    void addGenreToFilm(Film film);

    Genre getGenreById(Integer id);
}
