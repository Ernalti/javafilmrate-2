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
import java.util.*;

@Slf4j
@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        List<Object[]> batchParams = new ArrayList<>();
        film.getGenres().forEach(g -> batchParams.add(new Integer[]{film.getId(), g.getId()}));
        jdbcTemplate.batchUpdate(sql,batchParams);
    }

    @Override
    public Genre getGenreById(Integer id) {
        Genre genre;
        try {
            String sql = "SELECT genre_id, name FROM genre WHERE genre_id=?";
            genre = jdbcTemplate.query(sql, this::mapRowToGenre, id).get(0);
        } catch (IndexOutOfBoundsException e) {
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
