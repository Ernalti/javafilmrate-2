package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Integer id);

    User createUser(User user);

    User updateUser(User user);

    User addFriend(User user, Integer friendId);

    User delFriend(User user, Integer friendId);

    List<User> getFriendsByUserId(Integer id);

    List<User> getCommonFriends(Integer id, Integer friendId);

    void clearUsers();

}