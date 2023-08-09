package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService{
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        List<Film> allFilms = filmStorage.getAllFilms();
        allFilms.sort((film1, film2) -> film2.getLikes().size() - film1.getLikes().size());
        if (allFilms.size() > count) {
            return allFilms.subList(0, count);
        } else {
            return allFilms;
        }
    }

    public Film create(Film film) {
        log.info("Попытка загрузки фильма. Фильм: {}",film);
        if (beforeFirstFilm(film.getReleaseDate())) {
            log.warn("Ошибка создания фильма. дата релиза фильма не может быть раньше 28 декабря 1895 года. Фильм: {}",film);
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        return filmStorage.createFilm(film);
    }

    public Film update(Film film) {
        log.info("Попытка обновления фильма. Фильм: {}",film);
        if (beforeFirstFilm(film.getReleaseDate())) {
            log.warn("Ошибка обновления фильма. дата релиза фильма не может быть раньше 28 декабря 1895 года. Фильм: {}",film);
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        return filmStorage.updateFilm(film);
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
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайка от пользователя с id " + userId + " для фильма с id " + id + "не найдено");
        }
        return filmStorage.delLike(film,userId);
    }

    public void clearAll() {
        log.info("Попытка удаления всех фильмов");
        filmStorage.clearFilms();
    }

    private boolean beforeFirstFilm(LocalDate date) {
        return date.isBefore(LocalDate.of(1895, 12, 28));
    }
}