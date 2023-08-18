package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.film_id, f.name, f.description,f.release, f.duration, f.mpa_id, m.NAME as mpa_name, " +
                "ARRAY_AGG(fg.GENRE_ID) AS genres_id, ARRAY_AGG(g.NAME) AS genres_name " +
                "FROM FILMS AS f " +
                "LEFT JOIN FILM_GENRE AS fg ON f.film_id=fg.film_id " +
                "LEFT JOIN GENRE AS g ON g.genre_id=fg.GENRE_ID " +
                "LEFT JOIN mpa AS m ON f.MPA_ID = m.MPA_ID " +
                "GROUP BY f.FILM_ID";
        log.info("Получение списка фильмов из бд");
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Integer filmId = Math.toIntExact(simpleJdbcInsert.executeAndReturnKey(toMapFilm(film)).longValue());
        film.setId(filmId);
        genreStorage.addGenreToFilm(film);
        log.info("фильм добавлен в базу. id:{}", filmId);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name=?, description=?, release=?, duration=?,mpa_id=? WHERE film_id=?";
        int rowCount = this.jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (rowCount == 0) {
            log.warn("Ошибка обновления пользователя в бд фильм: {}", film);
            throw new NotFoundException("Не найден фильм с id = " + film.getId());
        }
        genreStorage.addGenreToFilm(film);
        log.info("Обновление фильма в бд прошло успешно. фильм: {}", film);
        //Пришлось так сделать. потому что в film попадают жанры в обратном порядке
        return getFilmById(film.getId());
    }

    @Override
    public Film addLike(Film film, Integer userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, film.getId(), userId);
        return film;
    }

    @Override
    public Film delLike(Film film, Integer userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId(), userId);
        return film;
    }

    @Override
    public void clearFilms() {
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT f.film_id, f.name, f.description,f.release, f.duration, f.mpa_id, m.NAME as mpa_name, " +
                "ARRAY_AGG(fg.GENRE_ID) AS genres_id, ARRAY_AGG(g.NAME) AS genres_name " +
                "FROM FILMS AS f " +
                "LEFT JOIN FILM_GENRE AS fg ON f.film_id=fg.film_id " +
                "LEFT JOIN GENRE AS g ON g.genre_id=fg.GENRE_ID " +
                "LEFT JOIN mpa AS m ON f.MPA_ID = m.MPA_ID " +
                "WHERE f.film_id=?" +
                "GROUP BY f.FILM_ID";
        log.info("Получение списка фильмов из бд");
        List<Film> result = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (result.size() == 0) {
            log.warn("Ошибка получения фильма по id. id: {}", id);
            throw new NotFoundException("Не найден фильм с id = " + id);
        }
        return result.get(0);
    }

    @Override
    public List<Film> getPopFilms(Integer count) {
        log.info("Обработка запроса на получение {} наиболее популярных фильмов из бд", count);
        String sql = "SELECT f.film_id, f.name, f.description,f.release, f.duration, f.mpa_id, m.NAME as mpa_name,  " +
                "    ARRAY_AGG(fg.GENRE_ID) AS genres_id, ARRAY_AGG(g.NAME) AS genres_name, COUNT(l.user_id) " +
                "FROM FILMS AS f " +
                "         LEFT JOIN FILM_GENRE AS fg ON f.film_id=fg.film_id " +
                "         LEFT JOIN GENRE AS g ON g.genre_id=fg.GENRE_ID " +
                "         LEFT JOIN mpa AS m ON f.MPA_ID = m.MPA_ID " +
                "         LEFT JOIN likes AS l ON f.film_id=l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id DESC LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        return result;
    }

    @Override
    public Film addLike(Integer id, Integer userId) {
        log.info("Попытка поставить лайк фильму в бд. Фильм: {}; Пользователь:{}", id, userId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, id, userId);
        Film film = getFilmById(id);
        return film;
    }

    @Override
    public Film delLike(Integer id, Integer userId) {
        log.info("Попытка удалить лайк фильму в бд. Фильм: {}; Пользователь:{}", id, userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        Film film = getFilmById(id);
        return film;
    }


    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Set<Genre> genres = new HashSet<>();

        Array genreIdsArray = rs.getArray("genres_id");
        Array genresNamesArray = rs.getArray("genres_name");
        if (genreIdsArray != null && genresNamesArray != null && ((Object[]) genreIdsArray.getArray())[0] != null) {
            genres = new HashSet<>();
            Object[] genreIdsArrayObj = (Object[]) genreIdsArray.getArray();
            Integer[] genreIds = Arrays.copyOf(genreIdsArrayObj, genreIdsArrayObj.length, Integer[].class);

            Object[] genresNamesArrayObj = (Object[]) genresNamesArray.getArray();
            String[] genresNames = Arrays.copyOf(genresNamesArrayObj, genresNamesArrayObj.length, String[].class);

            for (int i = genreIds.length - 1; i >= 0; i--) {
                genres.add(new Genre(genreIds[i], genresNames[i]));
            }
        }
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .genres(genres)
                .build();
        return film;
    }

    private Map<String, Object> toMapFilm(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_id", film.getMpa().getId());
        return values;
    }
}
