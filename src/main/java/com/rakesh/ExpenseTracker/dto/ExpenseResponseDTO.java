package com.rakesh.ExpenseTracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExpenseResponseDTO {

    private String spendOn;
    private BigDecimal amount;
    private LocalDateTime dateAndTime;
}