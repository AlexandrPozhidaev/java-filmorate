package ru.yandex.practicum.filmorate.exceptions;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private List<String> errors;

    public ErrorResponse(String message) {
        this.message = message;
        this.status = 0;
        this.errors = List.of();
    }

    public ErrorResponse(String message, int status, List<String> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public ErrorResponse() {
    }
}