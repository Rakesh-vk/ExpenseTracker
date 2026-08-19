package com.rakesh.ExpenseTracker.controller;

import com.rakesh.ExpenseTracker.dto.ErrorResponseDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/Expense")
@Tag(
        name = "Expense Management",
        description = "APIs for managing expenses"
)
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {


    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(
            summary = "Get all expenses",
            description = "Retrieves all expenses from the system."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Fetch all expense records",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = ExpenseResponseDTO.class
                            )
                    )
            )
    )
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(){
        log.debug("GET /Expense - fetching all expenses");
        return new ResponseEntity<>(expenseService.getAllData(), HttpStatus.OK);
    }
    @Operation(
            summary = "Get expenses By ID",
            description = "Retrieves expenses by ID from the system."
    )
    @Parameter(
            name = "id",
            description = "The ID of the expense to retrieve",
            required = true
    )
    @ApiResponse(
            responseCode = "200",
            description = "Fetch expense by ID",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ExpenseResponseDTO.class
                    )
            )

    )
    @ApiResponse(
            responseCode = "404",
            description = "Expense not found for the given ID",
            content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                    implementation = ErrorResponseDTO.class
            )
    )
    )

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(
            @PathVariable Long id) {
        log.debug("GET /Expense/{} - fetching expense", id);
        ExpenseResponseDTO responseDTO = expenseService.getDataById(id);
        return ResponseEntity.ok(responseDTO);
    }
    @Operation(
            summary = "Create new expense",
            description = "Takes ExpenseRequestDTO as input and creates a new entry",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Expense data to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ExpenseRequestDTO.class
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Expense created successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ExpenseResponseDTO.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid expense data",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseDTO.class
                    )
            )
    )
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> saveExpense(
            @Valid @RequestBody ExpenseRequestDTO requestDTO) {
        log.debug("POST /Expense - creating expense");

        ExpenseResponseDTO responseDTO =
                expenseService.saveData(requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
    @Operation(
            summary = "Update existing expense",
            description = "Takes ExpenseRequestDTO and id as input and updates the existing expense",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Expense data to update",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ExpenseRequestDTO.class
                            )
                    )
            )
    )
    @Parameter(
            name = "id",
            description = "The ID of the expense to update",
            required = true
    )
    @ApiResponse(
            responseCode = "200",
            description = "Expense updated successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ExpenseResponseDTO.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid expense data",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseDTO.class
                    )
            )
    )@ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseDTO.class
                    )
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
             @PathVariable Long id,
             @Valid @RequestBody ExpenseRequestDTO requestDTO) {
        log.debug("PUT /Expense/{} - updating expense", id);

        ExpenseResponseDTO responseDTO =
                expenseService.updateData(id, requestDTO);

        return ResponseEntity.ok(responseDTO);
    }
    @Operation(
            summary = "Delete existing expense",
            description = "Takes id as input and deletes the existing expense"

    )
    @Parameter(
            name = "id",
            description = "The ID of the expense to delete",
            required = true
    )
    @ApiResponse(
            responseCode = "204",
            description = "Expense deleted successfully"

    )
    @ApiResponse(
            responseCode = "404",
            description = "Expense not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseDTO.class
                    )
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id) {
        log.debug("DELETE /Expense/{} - deleting expense", id);

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }
}
