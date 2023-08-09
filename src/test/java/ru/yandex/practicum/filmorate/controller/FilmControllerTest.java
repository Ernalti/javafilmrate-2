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
                .build();
        HttpResponse<String> response = httpMethods.put("/films", gson.toJson(film2));
        assertEquals(500,response.statusCode());
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
                .build();
        HttpResponse<String> response = httpMethods.post("/films", gson.toJson(film));
        assertEquals(400,response.statusCode());
        Film[] films = gson.fromJson(httpMethods.get("/films").body(), Film[].class);
        assertEquals(0,films.length);
    }


}
