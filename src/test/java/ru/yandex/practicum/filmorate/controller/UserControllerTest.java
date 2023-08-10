package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest {

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
        httpMethods.del("/users");
    }

    @Test
    public void shouldCreateUser() throws IOException, InterruptedException {
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
        User user = User.builder()
                .login("UserLogin")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        assertEquals(200,response.statusCode());
        users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(1,users.length);
        user.setId(users[0].getId());
        assertEquals(user.toString(),users[0].toString());
    }

    @Test
    public void shouldCreateUserWithNullName() throws IOException, InterruptedException {
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
        User user = User.builder()
                .login("UserLogin")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        assertEquals(200,response.statusCode());
        users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(1,users.length);
        user.setId(users[0].getId());
        user.setName(user.getLogin());
        assertEquals(user.toString(),users[0].toString());
    }

    @Test
    public void shouldNotCreateUserWithEmptyLogin() throws IOException, InterruptedException {
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
        User user = User.builder()
                .login("")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        assertEquals(400,response.statusCode());
        users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
    }

    @Test
    public void shouldNotCreateUserWithNullLogin() throws IOException, InterruptedException {
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
        User user = User.builder()
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        assertEquals(400,response.statusCode());
        users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
    }

    @Test
    public void shouldNotCreateUserWithWrongEmail() throws IOException, InterruptedException {
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
        User user = User.builder()
                .login("")
                .name("UserName")
                .email("ern@ltinaor@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        assertEquals(400,response.statusCode());
        users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);
    }

    @Test
    public void shouldNotCreateUserWithWrongBirthDay() throws IOException, InterruptedException {
        User user = User.builder()
                .id(999)
                .login("UserLogin")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(2025,12,28))
                .build();
        HttpResponse<String> response = httpMethods.post("/users", gson.toJson(user));
        assertEquals(400,response.statusCode());
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(0,users.length);

    }

    @Test
    public void shouldUpdateUser() throws IOException, InterruptedException {
        User user = User.builder()
                .login("UserLogin")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        httpMethods.post("/users", gson.toJson(user));
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(1,users.length);
        User user2 = User.builder()
                .id(users[0].getId())
                .login("UserLogin11")
                .name("UserName")
                .email("user1@mail.ru")
                .birthday(LocalDate.of(1995,12,28))
                .build();
        HttpResponse<String> response = httpMethods.put("/users", gson.toJson(user2));
        assertEquals(200,response.statusCode());
        users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(1,users.length);
        user.setId(users[0].getId());
        assertEquals(user2.toString(),users[0].toString());
    }

    @Test
    public void shouldNotUpdateUserWithWrongId() throws IOException, InterruptedException {
        User user = User.builder()
                .login("UserLogin")
                .name("UserName")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1895,12,28))
                .build();
        httpMethods.post("/users", gson.toJson(user));
        User user2 = User.builder()
                .id(999)
                .login("UserLogin11")
                .name("UserName")
                .email("user1@mail.ru")
                .birthday(LocalDate.of(1995,12,28))
                .build();
        HttpResponse<String> response = httpMethods.put("/users", gson.toJson(user2));
        assertEquals(500,response.statusCode());
        User[] users = gson.fromJson(httpMethods.get("/users").body(), User[].class);
        assertEquals(1,users.length);
        user.setId(users[0].getId());
        assertEquals(user.toString(),users[0].toString());
    }
}
