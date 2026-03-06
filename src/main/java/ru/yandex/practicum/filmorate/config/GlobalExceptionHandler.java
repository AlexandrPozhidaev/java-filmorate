package ru.yandex.practicum.filmorate.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.controller.FilmController.log;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "Ошибка валидации";
            }
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    // Обработка всех остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Произошла внутренняя ошибка сервера");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("exception", ex.getClass().getSimpleName());
        log.error("Неожиданное исключение: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
}
