package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    static String serverUrl = "http://localhost:8080";
    private static Gson gson;
    static HttpMethods httpMethods;
    private final MpaService mpaService;
    private final GenreService genreService;

    @BeforeAll
    public static void beforeAll() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
        httpMethods = new HttpMethods(serverUrl);
    }


    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        httpMethods.del("/films");
    }

    @Test
    public void shouldCreateFilm() throws IOException, InterruptedException {
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0, films.length);
        Set<Genre> genres = new LinkedHashSet<>();
        genres.add(genreService.findGenre(1));
        genres.add(genreService.findGenre(3));
        Film film = Film.builder()
                .name("L' Arriv?e d'un train ? la Ciotat")
                .description("L'arriv?e d'un train en gare de La Ciotat est un film de 50 secondes r?alis? en 1896.")
                .duration(48L)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(200,response.statusCode());
        films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(1,films.length);
        film.setId(films[0].getId());
        assertEquals(film.toString(),films[0].toString());
    }

    @Test
    public void shouldNotCreateFilmWithEmptyName() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("")
                .description("L'arriv?e d'un train en gare de La Ciotat est un film de 50 secondes r?alis? en 1896.")
                .duration(48L)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(400,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void shouldNotCreateFilmWithNullName() throws IOException, InterruptedException {
        String film2 = "{\"description\":\"adipisicing\",\"releaseDate\":\"1967-03-25\",\"duration\":100}";
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film2));
        assertEquals(500, response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void souldNotCreateFilmWthWrongDate() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("L' Arriv?e d'un train ? la Ciotat")
                .description("L'arriv?e d'un train en gare de La Ciotat est un film de 50 secondes r?alis? en 1896.")
                .duration(48L)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(400,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void souldNotCreateFilmWithLongDescription() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("Film name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .duration(48L)
                .releaseDate(LocalDate.of(1980, 3, 25))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(400,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void shouldUpdateFilm() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("Film name")
                .description("description")
                .duration(100L)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        httpMethods.post("/films", gson.toJson(film));
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(1,films.length);
        film.setId(films[0].getId());
        assertEquals(film.toString(),films[0].toString());
        film = Film.builder()
                .id(films[0].getId())
                .name("Update film name")
                .description("update description")
                .duration(200L)
                .releaseDate(LocalDate.of(1994, 11, 2))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.put("/films", gson.toJson(film));
        assertEquals(200,response.statusCode());
        films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(1,films.length);

        assertEquals(film.toString(),films[0].toString());
    }

    @Test
    public void souldNotUdateFilmWithWrongId() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("Film name")
                .description("description")
                .duration(100L)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        httpMethods.post("/films", gson.toJson(film));
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(1,films.length);
        film.setId(films[0].getId());
        assertEquals(film.toString(),films[0].toString());
        Film film2 = Film.builder()
                .id(999)
                .name("Update film name")
                .description("update description")
                .duration(200L)
                .releaseDate(LocalDate.of(1994, 11, 2))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.put("/films", gson.toJson(film2));
        assertEquals(404,response.statusCode());
        films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(1,films.length);

        assertEquals(film.toString(),films[0].toString());
    }

    @Test
    public void souldNotCreateFilmWithWrongDuration() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("Film name")
                .description("description")
                .duration(-1L)
                .releaseDate(LocalDate.of(1995, 12, 27))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(400,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void shouldGetPopFilms() throws IOException, InterruptedException {
        // Создаем и добавляем тестовые фильмы в хранилище
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .duration(100L)
                .releaseDate(LocalDate.of(2023, 7, 1))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        Film film2 = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .duration(120L)
                .releaseDate(LocalDate.of(2023, 7, 15))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        Film film3 = Film.builder()
                .name("Film 3")
                .description("Description 3")
                .duration(90L)
                .releaseDate(LocalDate.of(2023, 7, 10))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> resp = httpMethods.post("/films", gson.toJson(film1));
        System.out.println(resp.body());
        Film res = gson.fromJson(resp.body(), Film.class);
        film1.setId(res.getId());
        res = gson.fromJson(httpMethods.post("/films", gson.toJson(film2)).body(), Film.class);
        film2.setId(res.getId());
        httpMethods.post("/films", gson.toJson(film3));
        User user = User.builder()
                .login("UserLogin")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895, 12, 28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        user.setId(gson.fromJson(httpMethods.get("/users").body(), User[].class)[0].getId());

        HttpResponse<String> addLikeResponse = httpMethods.put("/films/" + film2.getId() + "/like/" + user.getId(), "");
        int count = 2;
        response = httpMethods.get("/films/popular?count=" + count);
        assertEquals(200, response.statusCode());

        // Преобразуем ответ в список фильмов и проверяем количество
        Film[] popularFilms = gson.fromJson(response.body(), Film[].class);
        assertEquals(count, popularFilms.length);

        // Проверяем, что фильмы отсортированы по популярности (по убыванию количества лайков)
        assertEquals(popularFilms[0],film2);
    }

    @Test
    public void shouldAddAndDeleteLike() throws IOException, InterruptedException {
        Film film1 = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .duration(100L)
                .releaseDate(LocalDate.of(2023, 7, 1))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        Film film2 = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .duration(120L)
                .releaseDate(LocalDate.of(2023, 7, 15))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        Film film3 = Film.builder()
                .name("Film 3")
                .description("Description 3")
                .duration(90L)
                .releaseDate(LocalDate.of(2023, 7, 10))
                .mpa(mpaService.findMpa(1))
                .genres(new LinkedHashSet<>())
                .build();
        HttpResponse<String> resp = httpMethods.post("/films", gson.toJson(film1));
        System.out.println(resp.body());
        Film res = gson.fromJson(resp.body(), Film.class);
        film1.setId(res.getId());
        res = gson.fromJson(httpMethods.post("/films", gson.toJson(film2)).body(), Film.class);
        film2.setId(res.getId());
        httpMethods.post("/films", gson.toJson(film3));


        User user1 = User.builder()
                .login("UserLogin")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895, 12, 28))
                .build();
        User user2 = User.builder()
                .login("UserLogin2")
                .name("UserName2")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(1895, 12, 28))
                .build();
        httpMethods.post("/users", gson.toJson(user1));
        httpMethods.post("/users", gson.toJson(user2));
        User[] responseUser = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        int userId1 = responseUser[0].getId();
        int userId2 = responseUser[1].getId();
        HttpResponse<String> addLikeResponse = httpMethods.put("/films/" + film1.getId() + "/like/"
                + userId1, "");
        assertEquals(200, addLikeResponse.statusCode());
        httpMethods.put("/films/" + film2.getId() + "/like/" + userId1, "");
        httpMethods.put("/films/" + film2.getId() + "/like/" + userId2, "");

        HttpResponse<String> response = httpMethods.get("/films/popular?count=3");
        assertEquals(200, response.statusCode());

        // Преобразуем ответ в список фильмов и проверяем количество
        Film[] popularFilms = gson.fromJson(response.body(), Film[].class);

        // Проверяем, что фильмы отсортированы по популярности (по убыванию количества лайков)
        assertEquals(popularFilms[0],film2);


        HttpResponse<String> delLikeResponse = httpMethods.del("/films/" + film2.getId() + "/like/" + userId1);
        assertEquals(200, delLikeResponse.statusCode());
        httpMethods.del("/films/" + film2.getId() + "/like/" + userId2);
        response = httpMethods.get("/films/popular?count=3");
        popularFilms = gson.fromJson(response.body(), Film[].class);

        // Проверяем, что фильмы отсортированы по популярности (по убыванию количества лайков)
        assertEquals(popularFilms[0],film1);
    }

}
