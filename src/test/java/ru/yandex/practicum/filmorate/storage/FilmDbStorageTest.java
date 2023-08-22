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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

	private final FilmDbStorage filmStorage;
	private final UserDbStorage userStorage;
	private final MpaDbStorage mpaStorage;
	private final JdbcTemplate jdbcTemplate;

	Film film1;
	Film film2;
	Film film3;

	User user1;
	User user2;
	User user3;

	Mpa mpa1;
	Mpa mpa2;
	Mpa mpa3;

	@BeforeEach
	public void beforeEach() {
		userStorage.clearUsers();
		filmStorage.clearFilms();
		user1 = User.builder()
				.login("user1")
				.name("user1name")
				.email("user1@mail.ru")
				.birthday(LocalDate.of(2000, 1, 1))
				.build();
		user2 = User.builder()
				.login("user2")
				.name("user2name")
				.email("user2@mail.ru")
				.birthday(LocalDate.of(2000, 2, 1))
				.build();
		user3 = User.builder()
				.login("user3")
				.name("user3name")
				.email("user3@mail.ru")
				.birthday(LocalDate.of(2000, 3, 1))
				.build();

		mpa1 = mpaStorage.getMpaById(1);
		mpa2 = mpaStorage.getMpaById(2);
		mpa3 = mpaStorage.getMpaById(3);

		film1 = Film.builder()
				.name("film1")
				.description("film1Desc")
				.releaseDate(LocalDate.of(1987, 6, 5))
				.duration(100L)
				.mpa(mpa1)
				.genres(new LinkedHashSet<>())
				.build();
		film2 = Film.builder()
				.name("film2")
				.description("film2Desc")
				.releaseDate(LocalDate.of(1988, 6, 5))
				.duration(200L)
				.mpa(mpa2)
				.genres(new LinkedHashSet<>())
				.build();
		film3 = Film.builder()
				.name("film3")
				.description("film3Desc")
				.releaseDate(LocalDate.of(1989, 6, 5))
				.duration(300L)
				.mpa(mpa3)
				.genres(new LinkedHashSet<>())
				.build();
	}

	@Test
	public void shouldCreateEmptyDb() {
		List<Film> films = new ArrayList<>();
		assertEquals(films, filmStorage.getAllFilms());
	}

	@Test
	public void shouldClearAllFilms() {
		filmStorage.createFilm(film1);
		filmStorage.createFilm(film2);
		filmStorage.createFilm(film3);
		filmStorage.clearFilms();
		List<Film> films = new ArrayList<>();
		assertEquals(films, filmStorage.getAllFilms());
	}

	@Test
	public void shouldAddFilmAndGetFilm() {
		int film1Id = filmStorage.createFilm(film1).getId();
		film1.setId(film1Id);
		Film newFilm = filmStorage.getFilmById(film1Id);
		assertEquals(film1, newFilm);
	}

	@Test
	public void shouldFindFilms() {
		int film1Id = filmStorage.createFilm(film1).getId();
		int film2Id = filmStorage.createFilm(film2).getId();
		int film3Id = filmStorage.createFilm(film3).getId();
		film1.setId(film1Id);
		film2.setId(film2Id);
		film3.setId(film3Id);
		List<Film> films = filmStorage.getAllFilms();
		assertEquals(film1, films.get(0));
		assertEquals(film2, films.get(1));
		assertEquals(film3, films.get(2));
	}

	@Test
	public void shouldUpdateFilm() {
		int film1Id = filmStorage.createFilm(film1).getId();
		film1.setId(film1Id);
		film2.setId(film1Id);
		filmStorage.updateFilm(film2);
		Film newFilm = filmStorage.getFilmById(film1Id);
		assertEquals(film2, newFilm);
	}

	@Test
	public void shouldAddAndDeleteLike() {
		int film1Id = filmStorage.createFilm(film1).getId();
		film1.setId(film1Id);
		int user1Id = userStorage.createUser(user1).getId();
		user1.setId(user1Id);
		filmStorage.addLike(film1, user1Id);
		String sql = "SELECT l.user_id AS likes " +
				"FROM FILMS AS f " +
				"         LEFT JOIN likes AS l ON f.film_id=l.film_id " +
				"GROUP BY f.film_id ";
		List<Integer> likes = jdbcTemplate.query(sql, this::mapRowToLikes);
		assertEquals(user1Id, likes.get(0));
		filmStorage.delLike(film1, user1Id);
		likes = jdbcTemplate.query(sql, this::mapRowToLikes);
		assertEquals(0, likes.get(0));
	}

	@Test
	public void getPopFilms() {
		int film1Id = filmStorage.createFilm(film1).getId();
		int film2Id = filmStorage.createFilm(film2).getId();
		int film3Id = filmStorage.createFilm(film3).getId();
		film1.setId(film1Id);
		film2.setId(film2Id);
		film3.setId(film3Id);
		int user1Id = userStorage.createUser(user1).getId();
		int user2Id = userStorage.createUser(user2).getId();
		int user3Id = userStorage.createUser(user3).getId();
		user1.setId(user1Id);
		user2.setId(user2Id);
		user3.setId(user3Id);

		filmStorage.addLike(film1, user1Id);
		filmStorage.addLike(film2, user1Id);
		filmStorage.addLike(film2, user2Id);
		filmStorage.addLike(film2, user3Id);
		filmStorage.addLike(film3, user2Id);
		filmStorage.addLike(film3, user3Id);

		List<Film> films = filmStorage.getPopFilms(3);
		assertEquals(film2, films.get(0));
		assertEquals(film3, films.get(1));
		assertEquals(film1, films.get(2));
	}

	@Test
	public void shouldErrorGetFilmWithWrongId() {
		filmStorage.createFilm(film1);
		assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(9999));
	}


	@Test
	public void shouldErrorUpdateFilmWithWrongId() {
		filmStorage.createFilm(film1);
		film2.setId(9999);
		assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(film2));
	}

	private Integer mapRowToLikes(ResultSet rs, int rowNum) throws SQLException {
		return rs.getInt("likes");

	}
}
