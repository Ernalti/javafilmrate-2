package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorage {
	private final ru.yandex.practicum.filmorate.storage.db.MpaDbStorage mpaStorage;
	private final JdbcTemplate jdbcTemplate;

	@Test
	public void ShouldGetMpaById() {
		Mpa mpa = Mpa.builder()
				.id(1)
				.name("G")
				.build();
		assertEquals(mpa, mpaStorage.getMpaById(1));
	}

	@Test
	public void shouldErrorGetMpaWithWrongId() {

		assertThrows(NotFoundException.class, () -> mpaStorage.getMpaById(876));
	}

	@Test
	public void ShouldGetAllMpa() {
		String sql = "SELECT * FROM mpa";
		List<Mpa> allMpa = jdbcTemplate.query(sql, this::mapRowToMpa);
		assertEquals(allMpa, mpaStorage.getAllMpa());
	}

	private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
		return Mpa.builder()
				.id(rs.getInt("mpa_id"))
				.name(rs.getString("name"))
				.build();
	}

}
