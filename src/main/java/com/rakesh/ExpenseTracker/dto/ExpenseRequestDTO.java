package com.rakesh.ExpenseTracker.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExpenseRequestDTO {
    private Long id;
    private String spendOn;
    private BigDecimal amount;
}