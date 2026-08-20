package com.rakesh.ExpenseTracker.Controller;

import com.rakesh.ExpenseTracker.controller.ExpenseController;
import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.service.ExpenseService;
import com.rakesh.ExpenseTracker.service.JwtService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    // =========================================================
    // GET ALL - PAGINATION
    // =========================================================

    @Test
    void shouldGetAllExpenses() throws Exception {

        ExpenseResponseDTO expense1 =
                new ExpenseResponseDTO(
                        1L,
                        "Food",
                        new BigDecimal("500"),
                        LocalDateTime.now()
                );

        ExpenseResponseDTO expense2 =
                new ExpenseResponseDTO(
                        2L,
                        "Travel",
                        new BigDecimal("1000"),
                        LocalDateTime.now()
                );


        List<ExpenseResponseDTO> expenses =
                List.of(
                        expense1,
                        expense2
                );


        Pageable pageable =
                PageRequest.of(0, 10);


        Page<ExpenseResponseDTO> expensePage =
                new PageImpl<>(
                        expenses,
                        pageable,
                        expenses.size()
                );


        when(
                expenseService.getAllData(
                        0,
                        10
                )
        ).thenReturn(expensePage);


        mockMvc.perform(
                        get("/Expense")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())

                // Page content
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(2)
                )

                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(1)
                )

                .andExpect(
                        jsonPath("$.content[0].spendOn")
                                .value("Food")
                )

                .andExpect(
                        jsonPath("$.content[0].amount")
                                .value(500)
                )

                .andExpect(
                        jsonPath("$.content[1].id")
                                .value(2)
                )

                .andExpect(
                        jsonPath("$.content[1].spendOn")
                                .value("Travel")
                )

                .andExpect(
                        jsonPath("$.content[1].amount")
                                .value(1000)
                )

                // Pagination metadata
                .andExpect(
                        jsonPath("$.number")
                                .value(0)
                )

                .andExpect(
                        jsonPath("$.size")
                                .value(10)
                )

                .andExpect(
                        jsonPath("$.totalElements")
                                .value(2)
                )

                .andExpect(
                        jsonPath("$.totalPages")
                                .value(1)
                );


        verify(expenseService)
                .getAllData(0, 10);

        log.info(
                "GET all expenses pagination test passed"
        );
    }


    // =========================================================
    // GET BY ID - SUCCESS
    // =========================================================

    @Test
    void shouldGetExpenseById() throws Exception {

        ExpenseResponseDTO response =
                new ExpenseResponseDTO(
                        1L,
                        "Food",
                        new BigDecimal("500"),
                        LocalDateTime.now()
                );

        when(
                expenseService.getDataById(1L)
        ).thenReturn(response);


        mockMvc.perform(
                        get("/Expense/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.spendOn")
                                .value("Food")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500)
                );


        verify(expenseService)
                .getDataById(1L);

        log.info(
                "GET expense by ID test passed"
        );
    }


    // =========================================================
    // GET BY ID - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404WhenExpenseNotFound()
            throws Exception {

        when(
                expenseService.getDataById(99L)
        ).thenThrow(
                new ExpenseNotFound(
                        "Expense not found with id: 99"
                )
        );


        mockMvc.perform(
                        get("/Expense/99")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Expense not found with id: 99"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(expenseService)
                .getDataById(99L);

        log.info(
                "GET non-existing expense test passed"
        );
    }


    // =========================================================
    // POST - SUCCESS
    // =========================================================

    @Test
    void shouldCreateExpense() throws Exception {

        ExpenseResponseDTO response =
                new ExpenseResponseDTO(
                        1L,
                        "Food",
                        new BigDecimal("500"),
                        LocalDateTime.now()
                );


        when(
                expenseService.saveData(
                        any(ExpenseRequestDTO.class)
                )
        ).thenReturn(response);


        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.spendOn")
                                .value("Food")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500)
                );


        verify(expenseService)
                .saveData(
                        any(ExpenseRequestDTO.class)
                );

        log.info(
                "POST create expense test passed"
        );
    }


    // =========================================================
    // POST - NEGATIVE AMOUNT
    // =========================================================

    @Test
    void shouldRejectNegativeAmount()
            throws Exception {

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": -500
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Amount must be greater than zero"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(
                expenseService,
                never()
        ).saveData(
                any(ExpenseRequestDTO.class)
        );


        log.info(
                "POST negative amount validation test passed"
        );
    }


    // =========================================================
    // POST - NULL AMOUNT
    // =========================================================

    @Test
    void shouldRejectNullAmount()
            throws Exception {

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": null
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Amount is required"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(
                expenseService,
                never()
        ).saveData(
                any(ExpenseRequestDTO.class)
        );


        log.info(
                "POST null amount validation test passed"
        );
    }


    // =========================================================
    // POST - EMPTY spendOn
    // =========================================================

    @Test
    void shouldRejectEmptySpendOn()
            throws Exception {

        String requestJson = """
                {
                    "spendOn": "",
                    "amount": 500
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Spend on is required"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(
                expenseService,
                never()
        ).saveData(
                any(ExpenseRequestDTO.class)
        );


        log.info(
                "POST empty spendOn validation test passed"
        );
    }


    // =========================================================
    // PUT - SUCCESS
    // =========================================================

    @Test
    void shouldUpdateExpense()
            throws Exception {

        ExpenseResponseDTO response =
                new ExpenseResponseDTO(
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
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.spendOn")
                                .value("Shopping")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(1000)
                );


        verify(expenseService)
                .updateData(
                        eq(1L),
                        any(ExpenseRequestDTO.class)
                );


        log.info(
                "PUT update expense test passed"
        );
    }


    // =========================================================
    // PUT - NEGATIVE AMOUNT
    // =========================================================

    @Test
    void shouldRejectNegativeAmountOnUpdate()
            throws Exception {

        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": -100
                }
                """;


        mockMvc.perform(
                        put("/Expense/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Amount must be greater than zero"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(
                expenseService,
                never()
        ).updateData(
                eq(1L),
                any(ExpenseRequestDTO.class)
        );


        log.info(
                "PUT negative amount validation test passed"
        );
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
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Expense not found with id 99"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(expenseService)
                .updateData(
                        eq(99L),
                        any(ExpenseRequestDTO.class)
                );


        log.info(
                "PUT non-existing expense test passed"
        );
    }


    // =========================================================
    // DELETE - SUCCESS
    // =========================================================

    @Test
    void shouldDeleteExpense()
            throws Exception {

        doNothing()
                .when(expenseService)
                .deleteExpense(1L);


        mockMvc.perform(
                        delete("/Expense/1")
                )
                .andExpect(
                        status().isNoContent()
                );


        verify(expenseService)
                .deleteExpense(1L);


        log.info(
                "DELETE expense test passed"
        );
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


        mockMvc.perform(
                        delete("/Expense/99")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Expense not found with id 99"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );


        verify(expenseService)
                .deleteExpense(99L);


        log.info(
                "DELETE non-existing expense test passed"
        );
    }
    @Test
    void shouldRejectNegativePage() throws Exception {

        mockMvc.perform(
                        get("/Expense")
                                .param("page", "-1")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Page must be greater than or equal to zero"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                expenseService,
                never()
        ).getAllData(any(Integer.class), any(Integer.class));
    }
    @Test
    void shouldRejectZeroSize() throws Exception {

        mockMvc.perform(
                        get("/Expense")
                                .param("page", "0")
                                .param("size", "0")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Size must be greater than zero"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                expenseService,
                never()
        ).getAllData(any(Integer.class), any(Integer.class));
    }
    @Test
    void shouldRejectSizeGreaterThan100() throws Exception {

        mockMvc.perform(
                        get("/Expense")
                                .param("page", "0")
                                .param("size", "101")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Size must not be greater than 100"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                expenseService,
                never()
        ).getAllData(any(Integer.class), any(Integer.class));
    }
}