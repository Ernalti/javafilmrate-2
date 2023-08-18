package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @Override
    public List<Film> getAllFilms() {
        log.info("Получение списка фильмов из памяти");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            log.warn("Ошибка получения фильма по id. id: {}", id);
            throw new NotFoundException("Не найден фильм с id = " + id);
        }
        return films.get(id);
    }

    @Override
    public List<Film> getPopFilms(Integer count) {
        log.info("Обработка запроса на получение {} наиболее популярных фильмов", count);
        List<Film> allFilms = getAllFilms();
        allFilms.sort((film1, film2) -> film2.getLikes().size() - film1.getLikes().size());
        if (allFilms.size() > count) {
            return allFilms.subList(0, count);
        } else {
            return allFilms;
        }
    }

    @Override
    public Film addLike(Integer id, Integer userId) {
        log.info("Попытка поставить лайк фильму. Фильм: {}; Пользователь:{}", id, userId);
        Film film = getFilmById(id);
//        getUserById(userId);
        return addLike(film, userId);
    }

    @Override
    public Film delLike(Integer id, Integer userId) {
        log.info("Попытка удалить лайк фильму. Фильм: {}; Пользователь:{}", id, userId);
        Film film = getFilmById(id);
//        getUserById(userId);
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайка от пользователя с id " + userId + " для фильма с id " + id + "не найдено");
        }
        return delLike(film, userId);
    }


    @Override
    public Film createFilm(Film film) {
        film.setId(nextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно загружен в память. Фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления фильма. Идентификатор не найден. Фильм: {}", film);
            throw new NotFoundException("Не найден фильм с id = " + film.getId());
        }

        films.replace(film.getId(),film);
        log.info("Обновление фильма в памяти прошло успешно. Фильм: {}", film);
        return film;
    }

    @Override
    public Film addLike(Film film, Integer userId) {
        film.addLike(userId);
        log.info("Поставлен лайк фильму. Фильм: {}; Пользователь:{}", film, userId);
        return film;
    }

    @Override
    public Film delLike(Film film, Integer userId) {
        film.delLike(userId);
        log.info("Удален лайк фильму. Фильм: {}; Пользователь:{}", film, userId);
        return film;
    }

    @Override
    public void clearFilms() {
        log.info("Удаление всех фильмов из памяти");
        films.clear();
    }

    private Integer nextId() {
        id++;
        log.debug("Изменение id фильмов: {}",id);
        return id;
    }


}