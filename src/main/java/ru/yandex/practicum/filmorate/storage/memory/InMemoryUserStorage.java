package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage  implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @Override
    public List<User> getAllUsers() {
        log.info("Получение списка пользователей из памяти");
        return new ArrayList<>(users.values());
    }

    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            log.warn("Ошибка получения пользователя по id. id: {}",id);
            throw new NotFoundException("Не найден пользователь с id = " + id);
        }
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(nextId());
        users.put(user.getId(),user);
        log.info("Добавление пользователя в память. Пользователь: {}",user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка обновления пользователя Пользователь: {}",user);
            throw new NotFoundException("Не найден пользователь с id = " + user.getId());
        }
        users.replace(user.getId(),user);
        log.info("Обновление пользователя в памяти прошло успешно. Пользователь: {}",user);
        return user;
    }

    @Override
    public User addFriend(User user, Integer friendId) {
        user.addFriend(friendId);
        log.info("Друг пользователю добавлен. Пользователь: {}", user);
        return user;
    }

    @Override
    public User delFriend(User user, Integer friendId) {
        log.info("Попытка удаления друга пользователю. Пользователь: {}",user);
        if (!user.getFriends().contains(friendId)) {
            log.info("У пользователя не было такого друга. Пользователь: {}", user);
            throw new NotFoundException("У пользователя " + user.getName() + " не было такого друга с id = "
                    + user.getId());
        }
        user.delFriend(friendId);
        log.info("Удаления друга у пользователя прошло успешно. Пользователь: {}; Друг: {}", id, friendId);
        return user;
    }

    @Override
    public List<User> getFriendsByUserId(Integer id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        log.info("Обработка запроса на получение списка общих друзей пользователей. Пользователь 1 {}; " +
                "Пользователь 2 {}", id, friendId);
        User user = getUserById(id);
        User friend = getUserById(friendId);

        Set<Integer> friends = new HashSet<>(user.getFriends());
        friends.retainAll(friend.getFriends());

        ArrayList<User> commonUserFriends = new ArrayList<>();
        for (Integer userId : friends) {
            commonUserFriends.add(getUserById(userId));
        }
        return commonUserFriends;
    }

    @Override
    public void clearUsers() {
        log.info("Удаление всех пользователей из памяти");
        users.clear();
    }


    private Integer nextId() {
        id++;
        log.debug("Изменение id пользователей: {}", id);
        return id;
    }


}