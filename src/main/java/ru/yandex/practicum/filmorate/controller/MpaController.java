package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> findAll() {
        log.info("Поступил запрос на получение всех рейтингов");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findMpa(@PathVariable int id) {
        log.info("Поступил запрос на получение рейтинга для фильма с id={}", id);
        return mpaService.findMpa(id);
    }
}
