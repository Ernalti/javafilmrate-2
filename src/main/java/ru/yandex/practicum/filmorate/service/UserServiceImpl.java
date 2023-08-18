package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        log.info("Обработка запроса на получение списка пользователей");
        return userStorage.getAllUsers();
    }

    public User findUser(Integer id) {
        return userStorage.getUserById(id);
    }

    public List<User> getFriends(Integer id) {
        log.info("Обработка запроса на получение друзей пользователя. Пользователя id:{}", id);
        return userStorage.getFriendsByUserId(id);
    }

    public List<User> getCommonFriends(Integer id, Integer friendId) {
        log.info("Обработка запроса на получение списка общих друзей пользователей. Пользователь 1 {}; " +
                "Пользователь 2 {}", id, friendId);
        return userStorage.getCommonFriends(id, friendId);
    }

    public User create(User user) {
        copyLoginToBlankName(user);
        log.info("Попытка создания пользователя. Пользователь: {}", user);
        return userStorage.createUser(user);
    }

    public User update(User user) {
        copyLoginToBlankName(user);
        log.info("Попытка обновление пользователя. Пользователь: {}",user);
        return userStorage.updateUser(user);
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("Попытка добавления друга пользователю. Пользователь: {}; Друг: {}",id,friendId);
        User user = userStorage.getUserById(id);
        return userStorage.addFriend(user, friendId);
    }

    public User delFriend(Integer id, Integer friendId) {
        log.info("Попытка удаления друга пользователю. Пользователь: {}; Друг: {}",id,friendId);
        User user = userStorage.getUserById(id);
        return userStorage.delFriend(user,friendId);
    }

    public void clearAll() {
        log.info("Обработка запроса на удаление всех пользователей");
        userStorage.clearUsers();
    }

    private void copyLoginToBlankName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}