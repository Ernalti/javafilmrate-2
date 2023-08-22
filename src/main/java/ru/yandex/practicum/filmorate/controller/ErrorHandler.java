package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import javax.validation.ConstraintViolationException;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    Gson gson = new Gson();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse(gson.toJson(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse(gson.toJson(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        log.debug("Получен статус 404 Not found {}", ex.getMessage(), ex);
        return new ResponseEntity<>(gson.toJson(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleDuplicateKey(DuplicateKeyException ex) {
        log.debug("Получен статус 404 Not found {}", ex.getMessage(), ex);
        return new ResponseEntity<>(gson.toJson(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.debug("Получен статус 404 Not found {}", ex.getMessage(), ex);
        return new ResponseEntity<>(gson.toJson(ex.getMessage()), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler
    public ResponseEntity<String> handleTrowable(Throwable ex) {
        log.debug("Получен статус 500 Interal Server Error {}", ex.getMessage(), ex);
        return new ResponseEntity<>(gson.toJson(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
