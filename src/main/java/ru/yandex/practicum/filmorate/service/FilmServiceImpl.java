package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage,
                           UserStorage userStorage,
                           GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
    }

    public List<Film> findAll() {
        log.info("Обработка запроса на получение списка фильмов");
        return filmStorage.getAllFilms();
    }

    public Film findFilm(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getPopFilms(Integer count) {
        log.info("Обработка запроса на получение {} наиболее популярных фильмов", count);
        return filmStorage.getPopFilms(count);
    }

    public Film create(Film film) {
        log.info("Попытка загрузки фильма. Фильм: {}",film);
        beforeFirstFilm(film);
        Film result = filmStorage.createFilm(film);
        result.setGenres(film.getGenres());
        genreStorage.addGenreToFilm(result);
        return result;
    }

    public Film update(Film film) {
        log.info("Попытка обновления фильма. Фильм: {}",film);
        beforeFirstFilm(film);
        Film result = filmStorage.updateFilm(film);
        result.setGenres(film.getGenres());
        genreStorage.addGenreToFilm(result);
        return result;
    }

    public Film addLike(Integer id, Integer userId) {
        log.info("Попытка поставить лайк фильму. Фильм: {}; Пользователь:{}", id, userId);
        Film film = filmStorage.getFilmById(id);
        userStorage.getUserById(userId);
        return filmStorage.addLike(film,userId);
    }

    public Film delLike(Integer id, Integer userId) {
        log.info("Попытка удалить лайк фильму. Фильм: {}; Пользователь:{}", id, userId);
        Film film = filmStorage.getFilmById(id);
        userStorage.getUserById(userId);
        return filmStorage.delLike(film,userId);
    }

    public void clearAll() {
        log.info("Попытка удаления всех фильмов");
        filmStorage.clearFilms();
    }

    private void beforeFirstFilm(Film film) {
        LocalDate date = film.getReleaseDate();
        if (date!=null && date.isBefore(FIRST_FILM_DATE)) {
            log.warn("Ошибка создания фильма. дата релиза фильма не может быть раньше {}. Фильм: {}",FIRST_FILM_DATE, film);
            throw new ValidationException("Дата релиза фильма не может быть раньше "+FIRST_FILM_DATE);
        }
    }
}