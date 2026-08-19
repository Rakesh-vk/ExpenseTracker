package com.rakesh.ExpenseTracker.Controller;

import com.rakesh.ExpenseTracker.controller.UserController;
import com.rakesh.ExpenseTracker.dto.UserRequestDTO;
import com.rakesh.ExpenseTracker.dto.UserResponseDTO;
import com.rakesh.ExpenseTracker.exception.UserAlreadyExistsException;
import com.rakesh.ExpenseTracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rakesh.ExpenseTracker.service.JwtService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // ADD THESE TWO
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;



    // =========================================================
    // REGISTER - SUCCESS
    // =========================================================

    @Test
    void shouldRegisterUser() throws Exception {

        UserResponseDTO response = new UserResponseDTO(
                1L,
                "rakesh",
                "rakesh@example.com",
                LocalDateTime.now()
        );

        when(userService.registerUser(any(UserRequestDTO.class)))
                .thenReturn(response);

        String requestJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("rakesh"))
                .andExpect(jsonPath("$.email")
                        .value("rakesh@example.com"))
                .andExpect(jsonPath("$.dateAndTime").exists());

        verify(userService)
                .registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - DUPLICATE EMAIL
    // =========================================================

    @Test
    void shouldReturn409WhenUserAlreadyExists() throws Exception {

        when(userService.registerUser(any(UserRequestDTO.class)))
                .thenThrow(
                        new UserAlreadyExistsException(
                                "User already exists with email: rakesh@example.com"
                        )
                );

        String requestJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(
                                "User already exists with email: rakesh@example.com"
                        ))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(userService)
                .registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - MISSING USERNAME
    // =========================================================

    @Test
    void shouldRejectMissingUsername() throws Exception {

        String requestJson = """
                {
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Username is required"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - BLANK USERNAME
    // =========================================================

    @Test
    void shouldRejectBlankUsername() throws Exception {

        String requestJson = """
                {
                    "username": "",
                    "email": "rakesh@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Username is required"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - MISSING EMAIL
    // =========================================================

    @Test
    void shouldRejectMissingEmail() throws Exception {

        String requestJson = """
                {
                    "username": "rakesh",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Email is required"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - INVALID EMAIL
    // =========================================================

    @Test
    void shouldRejectInvalidEmail() throws Exception {

        String requestJson = """
                {
                    "username": "rakesh",
                    "email": "invalid-email",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid email format"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - BLANK EMAIL
    // =========================================================

    @Test
    void shouldRejectBlankEmail() throws Exception {

        String requestJson = """
                {
                    "username": "rakesh",
                    "email": "",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Email is required"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - MISSING PASSWORD
    // =========================================================

    @Test
    void shouldRejectMissingPassword() throws Exception {

        String requestJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Password is required"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }


    // =========================================================
    // REGISTER - PASSWORD TOO SHORT
    // =========================================================

    @Test
    void shouldRejectShortPassword() throws Exception {

        String requestJson = """
                {
                    "username": "rakesh",
                    "email": "rakesh@example.com",
                    "password": "1234567"
                }
                """;

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Password must be at least 8 characters"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(
                userService,
                never()
        ).registerUser(any(UserRequestDTO.class));
    }
}