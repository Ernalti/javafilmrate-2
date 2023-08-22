package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> findAll() {
        log.info("Обработка запроса на получение списка рейтингов");
        return mpaStorage.getAllMpa();
    }

    @Override
    public Mpa findMpa(Integer id) {
        log.info("Обработка запроса на получение рейтинга с id:{}", id);
        return mpaStorage.getMpaById(id);
    }
}
