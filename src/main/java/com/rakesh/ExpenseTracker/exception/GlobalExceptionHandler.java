package com.rakesh.ExpenseTracker.exception;

import com.rakesh.ExpenseTracker.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ExpenseNotFound.class)
    public ResponseEntity<ErrorResponseDTO> handleExpenseNotFound(
            ExpenseNotFound ex) {
        ErrorResponseDTO errorResponseDTO= new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(),ex.getMessage(), LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponseDTO);

    }

    // bean validation exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex) {


        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");
        ErrorResponseDTO errorResponseDTO= new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(),message, LocalDateTime.now());


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDTO);
    }
}