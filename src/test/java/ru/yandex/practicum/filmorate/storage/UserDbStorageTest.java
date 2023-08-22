package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

	private final UserDbStorage userStorage;
	private User user1;
	private User user2;
	private User user3;

	@BeforeEach
	public void beforeEach() {
		userStorage.clearUsers();
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
	}

	@Test
	public void souldCreateEmptyDb() {
		List<User> users = new ArrayList<>();
		assertEquals(users, userStorage.getAllUsers());
	}

	@Test
	void shouldClearAllUsers() {
		userStorage.createUser(user1);
		userStorage.createUser(user2);
		userStorage.createUser(user3);
		userStorage.clearUsers();
		List<User> users = new ArrayList<>();
		assertEquals(users, userStorage.getAllUsers());
	}

	@Test
	public void souldAddAndGetUser() {
		int user1Id = userStorage.createUser(user1).getId();
		user1.setId(user1Id);
		assertEquals(user1, userStorage.getUserById(user1Id));
	}

	@Test
	public void souldFindAllUsers() {
		int user1Id = userStorage.createUser(user1).getId();
		int user2Id = userStorage.createUser(user2).getId();
		int user3Id = userStorage.createUser(user3).getId();
		user1.setId(user1Id);
		user2.setId(user2Id);
		user3.setId(user3Id);
		List<User> users = userStorage.getAllUsers();
		assertEquals(user1, users.get(0));
		assertEquals(user2, users.get(1));
		assertEquals(user3, users.get(2));
	}

	@Test
	public void shouldUpdateUser() {
		int user1Id = userStorage.createUser(user1).getId();
		user1.setId(user1Id);
		user3.setId(user1Id);
		userStorage.updateUser(user3);
		assertEquals(user3, userStorage.getUserById(user1Id));
	}

	@Test
	public void souldAddDeleteGetFriends() {
		int user1Id = userStorage.createUser(user1).getId();
		int user2Id = userStorage.createUser(user2).getId();
		int user3Id = userStorage.createUser(user3).getId();
		user1.setId(user1Id);
		user2.setId(user2Id);
		user3.setId(user3Id);
		userStorage.addFriend(user1, user2Id);
		userStorage.addFriend(user1, user3Id);
		List<User> users = userStorage.getFriendsByUserId(user1Id);
		assertEquals(user2, users.get(0));
		assertEquals(user3, users.get(1));
		userStorage.delFriend(user1, user2Id);
		userStorage.delFriend(user1, user3Id);
	}

	@Test
	public void shouldGetCommonFriends() {
		int user1Id = userStorage.createUser(user1).getId();
		int user2Id = userStorage.createUser(user2).getId();
		int user3Id = userStorage.createUser(user3).getId();
		user1.setId(user1Id);
		user2.setId(user2Id);
		user3.setId(user3Id);
		userStorage.addFriend(user1, user3Id);
		userStorage.addFriend(user2, user3Id);
		List<User> users = userStorage.getCommonFriends(user1Id, user2Id);
		assertEquals(user3, users.get(0));
	}

	@Test
	public void errorUpdateUserWrongId() {
		userStorage.createUser(user1);
		user1.setId(1);
		user2.setId(9999);
		assertThrows(NotFoundException.class, () -> userStorage.updateUser(user2));
	}

	@Test
	public void errorAddFriendWrongId() {
		userStorage.createUser(user1);
		user1.setId(1);
		user2.setId(9999);
		assertThrows(NotFoundException.class, () -> userStorage.addFriend(user1, 9999));
	}


}
