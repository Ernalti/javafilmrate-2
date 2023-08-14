package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Поступил запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        log.info("Поступил запрос на получение пользователя с id={}",id);
        return userService.findUser(id);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriend(@PathVariable Integer id) {
        log.info("Поступил запрос на получение списка друзей");
        return userService.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriend(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("Поступил запрос на получение списка общих друзей");
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил запрос на создание пользователя {}",user.getName());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя {}",user.getName());
        return userService.update(user);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Поступил запрос на добавление друга с id={} для пользователя с id={}",friendId,id);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public User delFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Поступил запрос на удаление друга с id={} для пользователя с id={}",friendId,id);
        return userService.delFriend(id, friendId);
    }

    @DeleteMapping
    public void clearAll() {
        log.info("Поступил запрос на удаление всех пользователей");
        userService.clearAll();
    }

}