package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        log.info("Получение списка пользователей из бд");
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getUserById(Integer id) {
        User user;
        try {
            String sql = "SELECT * FROM users WHERE user_id=?";
            user = jdbcTemplate.query(sql, this::mapRowToUser, id).get(0);
        } catch (IndexOutOfBoundsException e) {
            log.warn("Ошибка получения пользователя по id из бд. id: {}", id);
            throw new NotFoundException("Не найден пользователь с id = " + id);
        }
        log.info("Получение пользователя из бд. id:{}", id);
        return user;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Integer userId = Math.toIntExact(simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue());
        user.setId(userId);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET login=?, name=?, email=?, birthday=? WHERE user_id=?";
        int rowCount = this.jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId());
        if (rowCount == 0) {
            log.warn("Ошибка обновления пользователя в бд Пользователь: {}", user);
            throw new NotFoundException("Не найден пользователь с id = " + user.getId());
        }
        log.info("Обновление пользователя в бд прошло успешно. Пользователь: {}", user);
        return user;
    }

    @Override
    public User addFriend(User user, Integer friendId) {
        Integer userId = user.getId();
        try {
            getUserById(userId);
            getUserById(friendId);
        } catch (NotFoundException e) {
            log.warn("Ошибка добавления друга пользователю в бд Пользователь: {}", user);
            throw new NotFoundException("Не найден пользователь с id = " + user.getId());
        }
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User delFriend(User user, Integer friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), friendId);
        return user;
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        getUserById(id);
        String sql = "SELECT user_id, login, name, email, birthday FROM users " +
                "WHERE user_id IN(SELECT friend_id FROM friends WHERE user_id=?)";
        List<User> result = jdbcTemplate.query(sql, this::mapRowToUser, id);
        return new ArrayList<>(result);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        String sqlQuery =
                "SELECT user_id, login, name, email, birthday FROM users " +
                        "WHERE user_id " +
                        "IN(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id = ?) " +
                        "AND user_id " +
                        "IN(SELECT friend_id " +
                        "FROM friends " +
                        "WHERE user_id = ?)";
        return new ArrayList<>(jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, friendId));
    }

    @Override
    public void clearUsers() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("birthday", user.getBirthday());
        return values;
    }
}
