package com.rakesh.ExpenseTracker;

import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }


    // =========================================================
    // REGISTER + LOGIN
    // =========================================================

    @Test
    void shouldRegisterAndLoginUser() throws Exception {

        // =========================
        // REGISTER
        // =========================

        String registerJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username")
                        .value("rakesh"))
                .andExpect(jsonPath("$.email")
                        .value("rakesh@example.com"));


        // =========================
        // VERIFY PASSWORD IS HASHED
        // =========================

        User user =
                userRepository.findByEmail(
                        "rakesh@example.com"
                ).orElseThrow();

        // Plain password must NOT be stored
        org.junit.jupiter.api.Assertions.assertNotEquals(
                "password123",
                user.getPassword()
        );

        // BCrypt hash should match original password
        org.junit.jupiter.api.Assertions.assertTrue(
                passwordEncoder.matches(
                        "password123",
                        user.getPassword()
                )
        );


        // =========================
        // LOGIN
        // =========================

        String loginJson = """
                {
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        String loginResponse =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message")
                                .value("Login successful"))
                        .andExpect(jsonPath("$.token")
                                .exists())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        // =========================
        // EXTRACT JWT
        // =========================

        JsonNode jsonNode =
                objectMapper.readTree(loginResponse);

        String token =
                jsonNode.get("token").asText();

        org.junit.jupiter.api.Assertions.assertFalse(
                token.isBlank()
        );
    }


    // =========================================================
    // PROTECTED ENDPOINT WITHOUT JWT
    // =========================================================

    @Test
    void shouldRejectRequestWithoutJwt() throws Exception {

        mockMvc.perform(
                        get("/Expense")
                )
                .andExpect(status().isUnauthorized());
    }


    // =========================================================
    // PROTECTED ENDPOINT WITH VALID JWT
    // =========================================================

    @Test
    void shouldAccessProtectedEndpointWithValidJwt()
            throws Exception {

        // =========================
        // REGISTER
        // =========================

        String registerJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

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
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        String loginResponse =
                mockMvc.perform(
                                post("/auth/login")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        JsonNode jsonNode =
                objectMapper.readTree(loginResponse);

        String token =
                jsonNode.get("token").asText();


        // =========================
        // ACCESS EXPENSE API
        // =========================

        mockMvc.perform(
                        get("/Expense")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }


    // =========================================================
    // WRONG PASSWORD
    // =========================================================

    @Test
    void shouldRejectWrongPassword() throws Exception {

        String registerJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJson)
                )
                .andExpect(status().isCreated());


        String loginJson = """
                {
                    "email": "rakesh@example.com",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"));
    }
}