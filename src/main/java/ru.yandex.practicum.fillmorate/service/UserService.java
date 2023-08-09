package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {
    List<User> findAll();

    User findUser(Integer id);
    List<User> getFriends(Integer id);

    List<User> getCommonFriends(Integer id, Integer friendId);

    User create(User user);

    User update(User user);

    User addFriend(Integer id, Integer friendId);

    User delFriend(Integer id, Integer friendId);

    void clearAll();

}