package com.rakesh.ExpenseTracker;

import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WithMockUser
@SpringBootTest
@AutoConfigureMockMvc
class ExpenseIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


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
                                .with(csrf())
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
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Amount must be greater than zero"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());
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
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Amount is required"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());
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
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Spend on is required"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());
    }


    @Test
    void shouldReturn404ForNonExistingExpense() throws Exception {

        mockMvc.perform(
                        get("/Expense/999999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Expense not found with id: 999999"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());
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
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Expense not found with id 999999"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());
    }


    @Test
    void shouldReturn404WhenDeletingNonExistingExpense()
            throws Exception {

        mockMvc.perform(
                        delete("/Expense/999999").with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Expense not found with id 999999"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());
    }
}