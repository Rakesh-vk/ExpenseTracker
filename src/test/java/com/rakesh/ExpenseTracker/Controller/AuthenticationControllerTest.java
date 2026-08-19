package com.rakesh.ExpenseTracker.Controller;

import com.rakesh.ExpenseTracker.controller.AuthenticationController;
import com.rakesh.ExpenseTracker.dto.LoginRequestDTO;
import com.rakesh.ExpenseTracker.dto.LoginResponseDTO;
import com.rakesh.ExpenseTracker.exception.InvalidCredentialsException;
import com.rakesh.ExpenseTracker.service.AuthenticationService;
import com.rakesh.ExpenseTracker.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    // =========================================================
    // LOGIN - SUCCESS
    // =========================================================

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginResponseDTO response =
                new LoginResponseDTO(
                        "Login successful",
                        "test-jwt-token"
                );

        when(authenticationService.login(
                any(LoginRequestDTO.class)
        )).thenReturn(response);

        String requestJson = """
                {
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"))
                .andExpect(jsonPath("$.token")
                        .value("test-jwt-token"));

        verify(authenticationService)
                .login(any(LoginRequestDTO.class));
    }


    // =========================================================
    // LOGIN - INVALID CREDENTIALS
    // =========================================================

    @Test
    void shouldReturn401ForInvalidCredentials() throws Exception {

        when(authenticationService.login(
                any(LoginRequestDTO.class)
        )).thenThrow(
                new InvalidCredentialsException(
                        "Invalid email or password"
                )
        );

        String requestJson = """
                {
                    "email": "rakesh@example.com",
                    "password": "wrongPassword"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email or password"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());

        verify(authenticationService)
                .login(any(LoginRequestDTO.class));
    }


    // =========================================================
    // LOGIN - MISSING EMAIL
    // =========================================================

    @Test
    void shouldRejectMissingEmail() throws Exception {

        String requestJson = """
                {
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Email is required"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());

        verify(
                authenticationService,
                never()
        ).login(any(LoginRequestDTO.class));
    }


    // =========================================================
    // LOGIN - INVALID EMAIL
    // =========================================================

    @Test
    void shouldRejectInvalidEmail() throws Exception {

        String requestJson = """
                {
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email format"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());

        verify(
                authenticationService,
                never()
        ).login(any(LoginRequestDTO.class));
    }


    // =========================================================
    // LOGIN - MISSING PASSWORD
    // =========================================================

    @Test
    void shouldRejectMissingPassword() throws Exception {

        String requestJson = """
                {
                    "email": "rakesh@example.com"
                }
                """;

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.message")
                        .value("Password is required"))
                .andExpect(jsonPath("$.timestamp")
                        .exists());

        verify(
                authenticationService,
                never()
        ).login(any(LoginRequestDTO.class));
    }
}