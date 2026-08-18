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
}