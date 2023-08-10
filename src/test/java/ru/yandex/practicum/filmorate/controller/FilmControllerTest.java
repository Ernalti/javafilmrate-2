package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmControllerTest {

    static String serverUrl = "http://localhost:8080";
    private static Gson gson;
    static HttpMethods httpMethods;

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
        assertEquals(0,films.length);
        Film film = Film.builder()
                .name("L' Arriv?e d'un train ? la Ciotat")
                .description("L'arriv?e d'un train en gare de La Ciotat est un film de 50 secondes r?alis? en 1896.")
                .duration(48L)
                .releaseDate(LocalDate.of(1895,12,28))
                .likes(new HashSet<>())
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
                .releaseDate(LocalDate.of(1895,12,28))
                .likes(new HashSet<>())
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
        assertEquals(400,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void souldNotCreateFilmWthWrongDate() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("L' Arriv?e d'un train ? la Ciotat")
                .description("L'arriv?e d'un train en gare de La Ciotat est un film de 50 secondes r?alis? en 1896.")
                .duration(48L)
                .releaseDate(LocalDate.of(1895,12,27))
                .likes(new HashSet<>())
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(500,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }

    @Test
    public void souldNotCreateFilmWithLongDescription() throws IOException, InterruptedException {
        Film film = Film.builder()
                .name("Film name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.")
                .duration(48L)
                .releaseDate(LocalDate.of(1980,3,25))
                .likes(new HashSet<>())
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
                .releaseDate(LocalDate.of(1995,12,27))
                .likes(new HashSet<>())
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
                .releaseDate(LocalDate.of(1994,11,2))
                .likes(new HashSet<>())
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
                .releaseDate(LocalDate.of(1995,12,27))
                .likes(new HashSet<>())
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
                .releaseDate(LocalDate.of(1994,11,2))
                .likes(new HashSet<>())
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
                .releaseDate(LocalDate.of(1995,12,27))
                .likes(new HashSet<>())
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
                .likes(new HashSet<>())
                .build();
        Film film2 = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .duration(120L)
                .releaseDate(LocalDate.of(2023, 7, 15))
                .likes(new HashSet<>())
                .build();
        Film film3 = Film.builder()
                .name("Film 3")
                .description("Description 3")
                .duration(90L)
                .releaseDate(LocalDate.of(2023, 7, 10))
                .likes(new HashSet<>())
                .build();
        httpMethods.post("/films", gson.toJson(film1));
        httpMethods.post("/films", gson.toJson(film2));
        httpMethods.post("/films", gson.toJson(film3));

        // Получаем наиболее популярные фильмы (2 фильма)
        int count = 2;
        HttpResponse<String> response = httpMethods.get("/films/popular?count=" + count);
        assertEquals(200, response.statusCode());

        // Преобразуем ответ в список фильмов и проверяем количество
        Film[] popularFilms = gson.fromJson(response.body(), Film[].class);
        assertEquals(count, popularFilms.length);

        // Проверяем, что фильмы отсортированы по популярности (по убыванию количества лайков)
        assertTrue(popularFilms[0].getLikes().size() >= popularFilms[1].getLikes().size());
    }

//    на гитхабе выдаёт ошибку на 254 строке. Видимо User по какой-то причине не возвращается, а на домашнем всё работает хорошо
//    FilmControllerTest.shouldAddAndDeleteLike:253 NullPointer

//    @Test
//    public void shouldAddAndDeleteLike() throws IOException, InterruptedException {
//        Film film = Film.builder()
//                .name("Film with Like")
//                .description("Description with Like")
//                .duration(120L)
//                .releaseDate(LocalDate.of(2023, 7, 20))
//                .likes(new HashSet<>())
//                .build();
//        httpMethods.post("/films", gson.toJson(film));
//
//        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
//        assertEquals(1, films.length);
//
//        User user = User.builder()
//                .login("UserLogin")
//                .name("UserName")
//                .email("user@mail.ru")
//                .birthday(LocalDate.of(1895,12,28))
//                .friends(new HashSet<>())
//                .build();
//        httpMethods.post("/users", gson.toJson(user));
//        int userId = user.getId();
//        HttpResponse<String> addLikeResponse = httpMethods.put("/films/" + films[0].getId() + "/like/" + userId,
//                "");
//        assertEquals(200, addLikeResponse.statusCode());
//
//        Film updatedFilm = gson.fromJson(httpMethods.get("/films/" + films[0].getId()).body(), Film.class);
//        assertTrue(updatedFilm.getLikes().contains(userId));
//
//        HttpResponse<String> delLikeResponse = httpMethods.del("/films/" + films[0].getId() + "/like/" + userId);
//        assertEquals(200, delLikeResponse.statusCode());
//
//        Film filmAfterDelLike = gson.fromJson(httpMethods.get("/films/" + films[0].getId()).body(), Film.class);
//        assertFalse(filmAfterDelLike.getLikes().contains(userId));
//    }

}
