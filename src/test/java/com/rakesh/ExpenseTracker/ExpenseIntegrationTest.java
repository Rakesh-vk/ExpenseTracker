package com.rakesh.ExpenseTracker;

import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@SpringBootTest
@AutoConfigureMockMvc
class ExpenseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldCreateAndFetchExpense() throws Exception {

        // =========================
        // POST
        // =========================

        String requestJson = """
                {
                    "spendOn": "Integration Test",
                    "amount": 500
                }
                """;

        String response = mockMvc.perform(
                        post("/Expense")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.spendOn")
                        .value("Integration Test"))
                .andExpect(jsonPath("$.amount")
                        .value(500))
                .andReturn()
                .getResponse()
                .getContentAsString();


        // =========================
        // Extract ID
        // =========================

        JsonNode jsonNode =
                objectMapper.readTree(response);

        Long id =
                jsonNode.get("id").asLong();


        // =========================
        // Verify database
        // =========================

        assertTrue(
                expenseRepository.findById(id).isPresent()
        );


        // =========================
        // GET
        // =========================

        mockMvc.perform(
                        get("/Expense/" + id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id))
                .andExpect(jsonPath("$.spendOn")
                        .value("Integration Test"))
                .andExpect(jsonPath("$.amount")
                        .value(500));
    }
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
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$").value(
                                "Amount must be greater than zero"
                        )
                );
    }
    @Test
    void shouldRejectMissingAmount() throws Exception {

        String requestJson = """
            {
                "spendOn": "Food"
            }
            """;

        mockMvc.perform(
                        post("/Expense")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$").value(
                                "Amount is required"
                        )
                );
    }
    @Test
    void shouldRejectBlankSpendOn() throws Exception {

        String requestJson = """
            {
                "spendOn": "",
                "amount": 500
            }
            """;

        mockMvc.perform(
                        post("/Expense")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$").value(
                                "Spend on is required"
                        )
                );
    }
    @Test
    void shouldReturn404ForNonExistingExpense() throws Exception {

        mockMvc.perform(
                        get("/Expense/999999")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$").value(
                                "Expense not found with id: 999999"
                        )
                );
    }
    @Test
    void shouldReturn404WhenUpdatingNonExistingExpense()
            throws Exception {

        String requestJson = """
            {
                "spendOn": "Food",
                "amount": 500
            }
            """;

        mockMvc.perform(
                        put("/Expense/999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$").value(
                                "Expense not found with id 999999"
                        )
                );
    }
    @Test
    void shouldReturn404WhenDeletingNonExistingExpense()
            throws Exception {

        mockMvc.perform(
                        delete("/Expense/999999")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$").value(
                                "Expense not found with id 999999"
                        )
                );
    }
}