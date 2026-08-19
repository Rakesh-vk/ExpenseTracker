package com.rakesh.ExpenseTracker.Service;

import com.rakesh.ExpenseTracker.dto.LoginRequestDTO;
import com.rakesh.ExpenseTracker.dto.LoginResponseDTO;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.InvalidCredentialsException;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import com.rakesh.ExpenseTracker.service.AuthenticationServiceImpl;
import com.rakesh.ExpenseTracker.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    private final UserRepository userRepository =
            mock(UserRepository.class);

    private final PasswordEncoder passwordEncoder =
            mock(PasswordEncoder.class);
    private final JwtService jwtService =
            mock(JwtService.class);

    private final AuthenticationServiceImpl authenticationService =
            new AuthenticationServiceImpl(
                    userRepository,
                    passwordEncoder,
                    jwtService
            );


    // =========================================================
    // LOGIN - SUCCESS
    // =========================================================

    @Test
    void shouldLoginWithValidCredentials() {

        LoginRequestDTO requestDTO = new LoginRequestDTO();

        requestDTO.setEmail("rakesh@example.com");
        requestDTO.setPassword("password123");


        User user = new User();

        user.setId(1L);
        user.setUsername("rakesh");
        user.setEmail("rakesh@example.com");
        user.setPassword("hashedPassword");


        when(userRepository.findByEmail("rakesh@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "hashedPassword"
        )).thenReturn(true);


        LoginResponseDTO response =
                authenticationService.login(requestDTO);


        assertNotNull(response);

        assertEquals(
                "Login successful",
                response.getMessage()
        );

        // JWT is not implemented yet
        assertEquals(
                null,
                response.getToken()
        );


        verify(userRepository)
                .findByEmail("rakesh@example.com");

        verify(passwordEncoder)
                .matches(
                        "password123",
                        "hashedPassword"
                );
    }


    // =========================================================
    // LOGIN - USER NOT FOUND
    // =========================================================

    @Test
    void shouldRejectUnknownEmail() {

        LoginRequestDTO requestDTO = new LoginRequestDTO();

        requestDTO.setEmail("unknown@example.com");
        requestDTO.setPassword("password123");


        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());


        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authenticationService.login(requestDTO)
                );


        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );


        verify(userRepository)
                .findByEmail("unknown@example.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }


    // =========================================================
    // LOGIN - WRONG PASSWORD
    // =========================================================

    @Test
    void shouldRejectWrongPassword() {

        LoginRequestDTO requestDTO = new LoginRequestDTO();

        requestDTO.setEmail("rakesh@example.com");
        requestDTO.setPassword("wrongPassword");


        User user = new User();

        user.setId(1L);
        user.setUsername("rakesh");
        user.setEmail("rakesh@example.com");
        user.setPassword("hashedPassword");


        when(userRepository.findByEmail("rakesh@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "hashedPassword"
        )).thenReturn(false);


        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authenticationService.login(requestDTO)
                );


        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );


        verify(userRepository)
                .findByEmail("rakesh@example.com");

        verify(passwordEncoder)
                .matches(
                        "wrongPassword",
                        "hashedPassword"
                );
    }
}