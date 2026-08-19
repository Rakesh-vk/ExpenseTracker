package com.rakesh.ExpenseTracker.exception;

import com.rakesh.ExpenseTracker.dto.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldHandleValidationErrors() {

        Object target = new Object();

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        target,
                        "expenseRequestDTO"
                );

        bindingResult.addError(
                new FieldError(
                        "expenseRequestDTO",
                        "amount",
                        "Amount must be greater than zero"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleValidationErrors(exception);

        // HTTP status
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        // Response body
        ErrorResponseDTO body = response.getBody();

        assertNotNull(body);

        // ErrorResponseDTO.status
        assertEquals(
                400,
                body.getStatus()
        );

        // ErrorResponseDTO.message
        assertEquals(
                "Amount must be greater than zero",
                body.getMessage()
        );

        // ErrorResponseDTO.timestamp
        assertNotNull(body.getTimestamp());
    }
}