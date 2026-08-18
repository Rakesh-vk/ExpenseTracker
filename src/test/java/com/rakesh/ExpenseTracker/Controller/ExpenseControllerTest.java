package com.rakesh.ExpenseTracker.Controller;

import com.rakesh.ExpenseTracker.controller.ExpenseController;
import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.service.ExpenseService;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllExpenses() throws Exception {

        ExpenseResponseDTO expense1 = new ExpenseResponseDTO(
                1L,
                "Food",
                new BigDecimal("500"),
                LocalDateTime.now()
        );

        ExpenseResponseDTO expense2 = new ExpenseResponseDTO(
                2L,
                "Travel",
                new BigDecimal("1000"),
                LocalDateTime.now()
        );

        when(expenseService.getAllData())
                .thenReturn(List.of(expense1, expense2));

        mockMvc.perform(get("/Expense"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].spendOn").value("Food"))
                .andExpect(jsonPath("$[0].amount").value(500))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].spendOn").value("Travel"))
                .andExpect(jsonPath("$[1].amount").value(1000));

        verify(expenseService).getAllData();

        log.info("GET all expenses test passed");
    }


    // =========================================================
    // GET BY ID - SUCCESS
    // =========================================================

    @Test
    void shouldGetExpenseById() throws Exception {

        ExpenseResponseDTO response = new ExpenseResponseDTO(
                1L,
                "Food",
                new BigDecimal("500"),
                LocalDateTime.now()
        );

        when(expenseService.getDataById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/Expense/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.spendOn").value("Food"))
                .andExpect(jsonPath("$.amount").value(500));

        verify(expenseService).getDataById(1L);

        log.info("GET expense by ID test passed");
    }


    // =========================================================
    // GET BY ID - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404WhenExpenseNotFound() throws Exception {

        when(expenseService.getDataById(99L))
                .thenThrow(
                        new ExpenseNotFound(
                                "Expense not found with id: 99"
                        )
                );

        mockMvc.perform(get("/Expense/99"))
                .andExpect(status().isNotFound())
                .andExpect(
                        content().string(
                                "Expense not found with id: 99"
                        )
                );

        verify(expenseService).getDataById(99L);

        log.info("GET non-existing expense test passed");
    }


    // =========================================================
    // POST - SUCCESS
    // =========================================================

    @Test
    void shouldCreateExpense() throws Exception {

        ExpenseResponseDTO response = new ExpenseResponseDTO(
                1L,
                "Food",
                new BigDecimal("500"),
                LocalDateTime.now()
        );

        when(expenseService.saveData(any(ExpenseRequestDTO.class)))
                .thenReturn(response);

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;

        mockMvc.perform(
                        post("/Expense")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.spendOn").value("Food"))
                .andExpect(jsonPath("$.amount").value(500));

        verify(expenseService)
                .saveData(any(ExpenseRequestDTO.class));

        log.info("POST create expense test passed");
    }


    // =========================================================
    // POST - NEGATIVE AMOUNT
    // =========================================================

    @Test
    void shouldRejectNegativeAmount() throws Exception {

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": -500
                }
                """;

        mockMvc.perform(
                        post("/Expense")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string(
                                "Amount must be greater than zero"
                        )
                );

        verify(
                expenseService,
                never()
        ).saveData(any(ExpenseRequestDTO.class));

        log.info("POST negative amount validation test passed");
    }


    // =========================================================
    // POST - NULL AMOUNT
    // =========================================================

    @Test
    void shouldRejectNullAmount() throws Exception {

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": null
                }
                """;

        mockMvc.perform(
                        post("/Expense")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string(
                                "Amount is required"
                        )
                );

        verify(
                expenseService,
                never()
        ).saveData(any(ExpenseRequestDTO.class));

        log.info("POST null amount validation test passed");
    }


    // =========================================================
    // POST - EMPTY spendOn
    // =========================================================

    @Test
    void shouldRejectEmptySpendOn() throws Exception {

        String requestJson = """
                {
                    "spendOn": "",
                    "amount": 500
                }
                """;

        mockMvc.perform(
                        post("/Expense")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string(
                                "Spend on is required"
                        )
                );

        verify(
                expenseService,
                never()
        ).saveData(any(ExpenseRequestDTO.class));

        log.info("POST empty spendOn validation test passed");
    }


    // =========================================================
    // PUT - SUCCESS
    // =========================================================

    @Test
    void shouldUpdateExpense() throws Exception {

        ExpenseResponseDTO response = new ExpenseResponseDTO(
                1L,
                "Shopping",
                new BigDecimal("1000"),
                LocalDateTime.now()
        );

        when(
                expenseService.updateData(
                        eq(1L),
                        any(ExpenseRequestDTO.class)
                )
        ).thenReturn(response);

        String requestJson = """
                {
                    "spendOn": "Shopping",
                    "amount": 1000
                }
                """;

        mockMvc.perform(
                        put("/Expense/1")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.spendOn").value("Shopping"))
                .andExpect(jsonPath("$.amount").value(1000));

        verify(expenseService)
                .updateData(
                        eq(1L),
                        any(ExpenseRequestDTO.class)
                );

        log.info("PUT update expense test passed");
    }


    // =========================================================
    // PUT - NEGATIVE AMOUNT
    // =========================================================

    @Test
    void shouldRejectNegativeAmountOnUpdate() throws Exception {

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": -100
                }
                """;

        mockMvc.perform(
                        put("/Expense/1")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().string(
                                "Amount must be greater than zero"
                        )
                );

        verify(
                expenseService,
                never()
        ).updateData(
                eq(1L),
                any(ExpenseRequestDTO.class)
        );

        log.info("PUT negative amount validation test passed");
    }


    // =========================================================
    // PUT - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404WhenUpdatingNonExistingExpense()
            throws Exception {

        when(
                expenseService.updateData(
                        eq(99L),
                        any(ExpenseRequestDTO.class)
                )
        ).thenThrow(
                new ExpenseNotFound(
                        "Expense not found with id 99"
                )
        );

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;

        mockMvc.perform(
                        put("/Expense/99")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        content().string(
                                "Expense not found with id 99"
                        )
                );

        verify(expenseService)
                .updateData(
                        eq(99L),
                        any(ExpenseRequestDTO.class)
                );

        log.info("PUT non-existing expense test passed");
    }


    // =========================================================
    // DELETE - SUCCESS
    // =========================================================

    @Test
    void shouldDeleteExpense() throws Exception {

        doNothing()
                .when(expenseService)
                .deleteExpense(1L);

        mockMvc.perform(delete("/Expense/1"))
                .andExpect(status().isNoContent());

        verify(expenseService)
                .deleteExpense(1L);

        log.info("DELETE expense test passed");
    }


    // =========================================================
    // DELETE - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404WhenDeletingNonExistingExpense()
            throws Exception {

        doThrow(
                new ExpenseNotFound(
                        "Expense not found with id 99"
                )
        )
                .when(expenseService)
                .deleteExpense(99L);

        mockMvc.perform(delete("/Expense/99"))
                .andExpect(status().isNotFound())
                .andExpect(
                        content().string(
                                "Expense not found with id 99"
                        )
                );

        verify(expenseService)
                .deleteExpense(99L);

        log.info("DELETE non-existing expense test passed");
    }
}