package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class GenresDbStorageTest {

	private final FilmDbStorage filmStorage;
	private final MpaDbStorage mpaStorage;
	private final JdbcTemplate jdbcTemplate;
	private final GenreDbStorage genreStorage;

	@BeforeEach
	public void beforeEach() {

	}

	@Test
	public void shouldGetGenreById() {
		Genre genre1 = genreStorage.getGenreById(2);
		Genre genre2 = Genre.builder()
				.id(2)
				.name("Драма")
				.build();
		assertEquals(genre2, genre1);
	}

	@Test
	public void shouldAddGetGenresFromFilm() {
		LinkedHashSet<Genre> genres = new LinkedHashSet<>();
		genres.add(genreStorage.getGenreById(1));
		Film film1 = Film.builder()
				.name("film1")
				.description("film1Desc")
				.releaseDate(LocalDate.of(1987, 6, 5))
				.duration(100L)
				.mpa(mpaStorage.getMpaById(1))
				.genres(new LinkedHashSet<>())
				.build();
		Integer filmId = filmStorage.createFilm(film1).getId();
		film1.setId(filmId);
		film1.setGenres(genres);
		genreStorage.addGenreToFilm(film1);
		Film newFilm = filmStorage.getFilmById(filmId);
		assertEquals(genres, newFilm.getGenres());
	}

	@Test
	public void shouldGetAllGenres() {
		String sql = "SELECT genre_id, name FROM genre";
		List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre);
		assertEquals(genres, genreStorage.getAllGenre());
	}

	@Test
	public void shouldErrorGetMpaWithWrongId() {

		assertThrows(NotFoundException.class, () -> genreStorage.getGenreById(876));
	}

	private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
		return Genre.builder()
				.id(rs.getInt("genre_id"))
				.name(rs.getString("name"))
				.build();
	}


}
