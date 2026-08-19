package com.rakesh.ExpenseTracker.dto;


import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExpenseRequestDTO {
    @Schema(
            description = "Describes what the expense amount was spent on",
            example = "Groceries"
    )
    @NotBlank(message = "Spend on is required")
    private String spendOn;
    @Schema(
            description = "The monetary amount of the expense",
            example = "100"
    )

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
}