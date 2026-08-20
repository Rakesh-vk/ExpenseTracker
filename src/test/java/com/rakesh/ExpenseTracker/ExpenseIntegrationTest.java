package com.rakesh.ExpenseTracker;

import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    // =========================================================
    // CLEAN DATABASE
    // =========================================================

    @BeforeEach
    void cleanDatabase() {

        expenseRepository.deleteAll();
        userRepository.deleteAll();
    }


    // =========================================================
    // REGISTER + LOGIN
    // =========================================================

    private String registerAndLogin() throws Exception {

        return registerAndLogin(
                "rakesh",
                "rakesh@example.com",
                "password123"
        );
    }


    // =========================================================
    // REGISTER + LOGIN - GENERIC
    // =========================================================

    private String registerAndLogin(
            String username,
            String email,
            String password) throws Exception {

        // =========================
        // REGISTER
        // =========================

        String registerJson = """
                {
                    "username": "%s",
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(
                username,
                email,
                password
        );


        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson)
                )
                .andExpect(status().isCreated());


        // =========================
        // LOGIN
        // =========================

        String loginJson = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(
                email,
                password
        );


        String loginResponse =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.token")
                                        .exists()
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        JsonNode jsonNode =
                objectMapper.readTree(loginResponse);


        String token =
                jsonNode.get("token").asText();


        assertTrue(
                token != null && !token.isBlank()
        );


        return token;
    }


    // =========================================================
    // CREATE + FETCH
    // =========================================================

    @Test
    void shouldCreateAndFetchExpense()
            throws Exception {

        String token =
                registerAndLogin();


        String requestJson = """
                {
                    "spendOn": "Integration Test",
                    "amount": 500
                }
                """;


        String response =
                mockMvc.perform(
                                post("/Expense")
                                        .with(
                                                org.springframework.security
                                                        .test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(requestJson)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(
                                jsonPath("$.spendOn")
                                        .value("Integration Test")
                        )
                        .andExpect(
                                jsonPath("$.amount")
                                        .value(500)
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        JsonNode jsonNode =
                objectMapper.readTree(response);


        Long id =
                jsonNode.get("id").asLong();


        assertTrue(
                expenseRepository
                        .findById(id)
                        .isPresent()
        );


        mockMvc.perform(
                        get("/Expense/" + id)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(id)
                )
                .andExpect(
                        jsonPath("$.spendOn")
                                .value("Integration Test")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500)
                );
    }


    // =========================================================
    // GET ALL - PAGINATION
    // =========================================================

    @Test
    void shouldGetAllExpensesForAuthenticatedUser()
            throws Exception {

        String token =
                registerAndLogin();


        String expense1 = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;


        String expense2 = """
                {
                    "spendOn": "Travel",
                    "amount": 1000
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(expense1)
                )
                .andExpect(status().isCreated());


        mockMvc.perform(
                        post("/Expense")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(expense2)
                )
                .andExpect(status().isCreated());


        // =====================================================
        // GET FIRST PAGE
        // =====================================================

        mockMvc.perform(
                        get("/Expense")
                                .param("page", "0")
                                .param("size", "10")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())

                // Page content
                .andExpect(
                        jsonPath("$.content.length()")
                                .value(2)
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
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateExpense()
            throws Exception {

        String token =
                registerAndLogin();


        String createJson = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;


        String response =
                mockMvc.perform(
                                post("/Expense")
                                        .with(
                                                org.springframework.security
                                                        .test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(createJson)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        Long id =
                objectMapper
                        .readTree(response)
                        .get("id")
                        .asLong();


        String updateJson = """
                {
                    "spendOn": "Shopping",
                    "amount": 1000
                }
                """;


        mockMvc.perform(
                        put("/Expense/" + id)
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(updateJson)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(id)
                )
                .andExpect(
                        jsonPath("$.spendOn")
                                .value("Shopping")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(1000)
                );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteExpense()
            throws Exception {

        String token =
                registerAndLogin();


        String createJson = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;


        String response =
                mockMvc.perform(
                                post("/Expense")
                                        .with(
                                                org.springframework.security
                                                        .test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(createJson)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        Long id =
                objectMapper
                        .readTree(response)
                        .get("id")
                        .asLong();


        mockMvc.perform(
                        delete("/Expense/" + id)
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isNoContent()
                );


        assertTrue(
                expenseRepository
                        .findById(id)
                        .isEmpty()
        );
    }


    // =========================================================
    // NEGATIVE AMOUNT
    // =========================================================

    @Test
    void shouldRejectNegativeAmount()
            throws Exception {

        String token =
                registerAndLogin();


        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": -500
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
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
    }


    // =========================================================
    // MISSING AMOUNT
    // =========================================================

    @Test
    void shouldRejectMissingAmount()
            throws Exception {

        String token =
                registerAndLogin();


        String requestJson = """
                {
                    "spendOn": "Food"
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
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
    }


    // =========================================================
    // BLANK SPEND ON
    // =========================================================

    @Test
    void shouldRejectBlankSpendOn()
            throws Exception {

        String token =
                registerAndLogin();


        String requestJson = """
                {
                    "spendOn": "",
                    "amount": 500
                }
                """;


        mockMvc.perform(
                        post("/Expense")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
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
    }


    // =========================================================
    // GET - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404ForNonExistingExpense()
            throws Exception {

        String token =
                registerAndLogin();


        mockMvc.perform(
                        get("/Expense/999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Expense not found with id 999999"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );
    }


    // =========================================================
    // UPDATE - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404WhenUpdatingNonExistingExpense()
            throws Exception {

        String token =
                registerAndLogin();


        String requestJson = """
                {
                    "spendOn": "Food",
                    "amount": 500
                }
                """;


        mockMvc.perform(
                        put("/Expense/999999")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
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
                                        "Expense not found with id 999999"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );
    }


    // =========================================================
    // DELETE - NOT FOUND
    // =========================================================

    @Test
    void shouldReturn404WhenDeletingNonExistingExpense()
            throws Exception {

        String token =
                registerAndLogin();


        mockMvc.perform(
                        delete("/Expense/999999")
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Expense not found with id 999999"
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );
    }


    // =========================================================
    // NO JWT
    // =========================================================

    @Test
    void shouldRejectRequestWithoutJwt()
            throws Exception {

        mockMvc.perform(
                        get("/Expense")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }


    // =========================================================
    // USER ISOLATION - GET
    // =========================================================

    @Test
    void shouldNotAllowUserToAccessAnotherUsersExpense()
            throws Exception {

        String tokenA =
                registerAndLogin(
                        "rakesh",
                        "rakesh@example.com",
                        "password123"
                );


        String tokenB =
                registerAndLogin(
                        "user2",
                        "user2@example.com",
                        "password123"
                );


        String expenseJson = """
                {
                    "spendOn": "User A Private Expense",
                    "amount": 1000
                }
                """;


        String response =
                mockMvc.perform(
                                post("/Expense")
                                        .with(
                                                org.springframework.security
                                                        .test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .header(
                                                "Authorization",
                                                "Bearer " + tokenA
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(expenseJson)
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        JsonNode jsonNode =
                objectMapper.readTree(response);


        Long expenseId =
                jsonNode.get("id").asLong();


        // User A can access

        mockMvc.perform(
                        get("/Expense/" + expenseId)
                                .header(
                                        "Authorization",
                                        "Bearer " + tokenA
                                )
                )
                .andExpect(status().isOk());


        // User B cannot access

        mockMvc.perform(
                        get("/Expense/" + expenseId)
                                .header(
                                        "Authorization",
                                        "Bearer " + tokenB
                                )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Expense not found with id "
                                                + expenseId
                                )
                );
    }


    // =========================================================
    // USER ISOLATION - UPDATE
    // =========================================================

    @Test
    void shouldNotAllowUserToUpdateAnotherUsersExpense()
            throws Exception {

        String tokenA =
                registerAndLogin(
                        "rakesh",
                        "rakesh@example.com",
                        "password123"
                );


        String tokenB =
                registerAndLogin(
                        "user2",
                        "user2@example.com",
                        "password123"
                );


        String createJson = """
                {
                    "spendOn": "User A Expense",
                    "amount": 1000
                }
                """;


        String response =
                mockMvc.perform(
                                post("/Expense")
                                        .with(
                                                org.springframework.security
                                                        .test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .header(
                                                "Authorization",
                                                "Bearer " + tokenA
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(createJson)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        Long expenseId =
                objectMapper
                        .readTree(response)
                        .get("id")
                        .asLong();


        String updateJson = """
                {
                    "spendOn": "Hacked Expense",
                    "amount": 1
                }
                """;


        mockMvc.perform(
                        put("/Expense/" + expenseId)
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + tokenB
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(updateJson)
                )
                .andExpect(
                        status().isNotFound()
                );


        // Verify User A's expense was not modified

        mockMvc.perform(
                        get("/Expense/" + expenseId)
                                .header(
                                        "Authorization",
                                        "Bearer " + tokenA
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.spendOn")
                                .value("User A Expense")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(1000)
                );
    }


    // =========================================================
    // USER ISOLATION - DELETE
    // =========================================================

    @Test
    void shouldNotAllowUserToDeleteAnotherUsersExpense()
            throws Exception {

        String tokenA =
                registerAndLogin(
                        "rakesh",
                        "rakesh@example.com",
                        "password123"
                );


        String tokenB =
                registerAndLogin(
                        "user2",
                        "user2@example.com",
                        "password123"
                );


        String createJson = """
                {
                    "spendOn": "User A Expense",
                    "amount": 1000
                }
                """;


        String response =
                mockMvc.perform(
                                post("/Expense")
                                        .with(
                                                org.springframework.security
                                                        .test.web.servlet
                                                        .request
                                                        .SecurityMockMvcRequestPostProcessors
                                                        .csrf()
                                        )
                                        .header(
                                                "Authorization",
                                                "Bearer " + tokenA
                                        )
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(createJson)
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        Long expenseId =
                objectMapper
                        .readTree(response)
                        .get("id")
                        .asLong();


        mockMvc.perform(
                        delete("/Expense/" + expenseId)
                                .with(
                                        org.springframework.security
                                                .test.web.servlet
                                                .request
                                                .SecurityMockMvcRequestPostProcessors
                                                .csrf()
                                )
                                .header(
                                        "Authorization",
                                        "Bearer " + tokenB
                                )
                )
                .andExpect(
                        status().isNotFound()
                );


        // Verify User A's expense still exists

        assertTrue(
                expenseRepository
                        .findById(expenseId)
                        .isPresent()
        );
    }
    // =========================================================
// PAGINATION - SECOND PAGE
// =========================================================

    @Test
    void shouldReturnSecondPageOfExpenses()
            throws Exception {

        String token =
                registerAndLogin();


        // Create 11 expenses

        for (int i = 1; i <= 11; i++) {

            String requestJson = """
                {
                    "spendOn": "Expense %d",
                    "amount": %d
                }
                """.formatted(i, i * 100);


            mockMvc.perform(
                            post("/Expense")
                                    .with(
                                            org.springframework.security
                                                    .test.web.servlet
                                                    .request
                                                    .SecurityMockMvcRequestPostProcessors
                                                    .csrf()
                                    )
                                    .header(
                                            "Authorization",
                                            "Bearer " + token
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(requestJson)
                    )
                    .andExpect(
                            status().isCreated()
                    );
        }


        // =====================================================
        // FIRST PAGE
        // =====================================================

        mockMvc.perform(
                        get("/Expense")
                                .param("page", "0")
                                .param("size", "10")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.content.length()")
                                .value(10)
                )

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
                                .value(11)
                )

                .andExpect(
                        jsonPath("$.totalPages")
                                .value(2)
                );


        // =====================================================
        // SECOND PAGE
        // =====================================================

        mockMvc.perform(
                        get("/Expense")
                                .param("page", "1")
                                .param("size", "10")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.content.length()")
                                .value(1)
                )

                .andExpect(
                        jsonPath("$.number")
                                .value(1)
                )

                .andExpect(
                        jsonPath("$.size")
                                .value(10)
                )

                .andExpect(
                        jsonPath("$.totalElements")
                                .value(11)
                )

                .andExpect(
                        jsonPath("$.totalPages")
                                .value(2)
                );
    }
}