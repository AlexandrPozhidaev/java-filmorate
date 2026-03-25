package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;
import ru.yandex.practicum.filmorate.model.request.UserRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // === ТЕСТЫ ДЛЯ FilmRequest ===

    @Test
    void testValidFilmRequest() {
        FilmRequest filmRequest = FilmRequest.builder()
                .name("Valid Film")
                .description("Valid description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertTrue(violations.isEmpty(), "Валидация должна проходить успешно для корректных данных FilmRequest");
    }

    @Test
    void testEmptyNameInFilmRequest() {
        FilmRequest filmRequest = FilmRequest.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertEquals(1, violations.size(), "Должно быть одно нарушение для пустого имени");

        ConstraintViolation<FilmRequest> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("не может быть пустым"),
                "Сообщение об ошибке должно содержать 'не может быть пустым'");
    }

    @Test
    void testNullNameInFilmRequest() {
        FilmRequest filmRequest = FilmRequest.builder()
                .name(null)
                .description("Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertEquals(1, violations.size());

        ConstraintViolation<FilmRequest> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertTrue(violation.getConstraintDescriptor().getAnnotation() instanceof NotBlank);
    }

    @Test
    void testDescriptionTooLongInFilmRequest() {
        String longDescription = "a".repeat(201);
        FilmRequest filmRequest = FilmRequest.builder()
                .name("Film")
                .description(longDescription)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertEquals(1, violations.size());

        ConstraintViolation<FilmRequest> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("Максимальная длина описания"),
                "Сообщение должно содержать информацию о максимальной длине");
    }

    @Test
    void testMaxDescriptionLengthInFilmRequest() {
        String maxDescription = "a".repeat(200);
        FilmRequest filmRequest = FilmRequest.builder()
                .name("Film")
                .description(maxDescription)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(120L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertTrue(violations.isEmpty(), "Описание максимальной длины должно проходить валидацию");
    }

    @Test
    void testNegativeDurationInFilmRequest() {
        FilmRequest filmRequest = FilmRequest.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(-10L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertEquals(1, violations.size());

        ConstraintViolation<FilmRequest> violation = violations.iterator().next();
        assertEquals("duration", violation.getPropertyPath().toString());
        assertTrue(violation.getConstraintDescriptor().getAnnotation() instanceof Positive);
    }

    @Test
    void testZeroDurationInFilmRequest() {
        FilmRequest filmRequest = FilmRequest.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(0L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertEquals(1, violations.size());

        ConstraintViolation<FilmRequest> violation = violations.iterator().next();
        assertEquals("duration", violation.getPropertyPath().toString());
        assertTrue(violation.getConstraintDescriptor().getAnnotation() instanceof Positive);
    }

    @Test
    void testMinDurationInFilmRequest() {
        FilmRequest filmRequest = FilmRequest.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2023, 1, 1))
                .duration(1L)
                .build();

        Set<ConstraintViolation<FilmRequest>> violations = validator.validate(filmRequest);
        assertTrue(violations.isEmpty(), "Минимальная длительность (1 секунда) должна проходить валидацию");
    }

    // === ТЕСТЫ ДЛЯ UserRequest ===

    @Test
    void testValidUserRequest() {
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .login("validlogin")
                .name("John Doe")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        assertTrue(violations.isEmpty(), "Валидация должна проходить успешно для корректных данных UserRequest");
    }

    @Test
    void testEmptyLoginInUserRequest() {
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .login("")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<UserRequest>> violations =
                validator.validate(userRequest, User.OnCreate.class);

        assertEquals(1, violations.size(),
                "Должно быть одно нарушение для пустого login в группе OnCreate");

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("login", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("не может быть пустым"));
    }

    @Test
    void testNullLoginInUserRequest() {
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .login(null)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<UserRequest>> violations =
                validator.validate(userRequest, User.OnCreate.class);

        assertEquals(1, violations.size(),
                "Должно быть одно нарушение для null login в группе OnCreate");

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("login", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("не может быть пустым"));
    }


      @Test
    void testValidEmailFormatInUserRequest() {
        UserRequest userRequest = UserRequest.builder()
                .email("valid.email+tag@example.co.uk")
                .login("validlogin")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        assertTrue(violations.isEmpty(), "Корректный формат email должен проходить валидацию");
    }

    @Test
    void testLongLoginInUserRequest() {
        String longLogin = "a".repeat(50); // Предполагаем, что лимит > 50
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .login(longLogin)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        assertTrue(violations.isEmpty(), "Длинный логин должен проходить валидацию, если не превышает лимита");
    }

    @Test
    void testMinLoginLengthInUserRequest() {
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .login("a") // Минимальная длина — 1 символ
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        assertTrue(violations.isEmpty(), "Логин минимальной длины должен проходить валидацию");
    }

    @Test
    void testTodayBirthdayInUserRequest() {
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .login("validlogin")
                .birthday(LocalDate.now())
                .build();

        Set<ConstraintViolation<UserRequest>> violations = validator.validate(userRequest);
        assertTrue(violations.isEmpty(), "Сегодняшняя дата рождения должна проходить валидацию");
    }
}