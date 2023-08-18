package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Genre> getGenreByFilm(Film film) {
        Integer id = film.getId();
        Set<Genre> genres;
        String sqlDel = "DELETE FROM film_genre WHERE film_id=?";
        String sql = "SELECT g.genre_id, g.name " +
                "FROM films AS f " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = ? " +
                "ORDER BY g.genre.id DESC";
        try {
            jdbcTemplate.update(sqlDel, film.getId());
            genres = new HashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre, id));
        } catch (Exception e) {
            log.warn("Ошибка получения жанра по фильму из бд. id фильма: {}", id);
            throw new NotFoundException("Не найден жанр фильма с id = " + id);
        }
        return genres;
    }

    @Override
    public List<Genre> getAllGenre() {
        String sql = "SELECT genre_id, name FROM genre";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public void addGenreToFilm(Film film) {
        if (film.getGenres() == null) {
            return;
        }
        String sqlDel = "DELETE FROM film_genre WHERE film_id = ?";
        String sql = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlDel, film.getId());
        film.getGenres().forEach(g -> jdbcTemplate.update(sql, film.getId(), g.getId()));
    }

    @Override
    public Genre getGenreById(Integer id) {
        Genre genre;
        try {
            String sql = "SELECT genre_id, name FROM genre WHERE genre_id=?";
            genre = jdbcTemplate.query(sql, this::mapRowToGenre, id).get(0);
        } catch (Exception e) {
            log.warn("Ошибка получения жанра по id: {}", id);
            throw new NotFoundException("Не найден жанр фильма с id = " + id);
        }
        return genre;
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
