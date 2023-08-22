package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> findAll() {
        log.info("Обработка запроса на получение списка рейтингов");
        return genreStorage.getAllGenre();
    }

    @Override
    public Genre findGenre(Integer id) {
        log.info("Обработка запроса на получение рейтинга с id:{}", id);
        return genreStorage.getGenreById(id);
    }


}
