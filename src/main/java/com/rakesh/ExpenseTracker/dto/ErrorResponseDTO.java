package com.rakesh.ExpenseTracker.dto;

import lombok.Getter;


import java.time.LocalDateTime;

@Getter

public class ErrorResponseDTO {
    public ErrorResponseDTO(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    private int status;
    private String message;
    private LocalDateTime timestamp;
}
