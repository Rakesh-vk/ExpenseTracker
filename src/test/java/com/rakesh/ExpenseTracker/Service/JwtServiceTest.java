package com.rakesh.ExpenseTracker.Service;

import com.rakesh.ExpenseTracker.service.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void shouldGenerateToken() {

        String token =
                jwtService.generateToken("rakesh@example.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }


    @Test
    void shouldExtractEmailFromToken() {

        String email = "rakesh@example.com";

        String token =
                jwtService.generateToken(email);

        String extractedEmail =
                jwtService.extractEmail(token);

        assertEquals(
                email,
                extractedEmail
        );
    }


    @Test
    void shouldReturnTrueForValidToken() {

        String token =
                jwtService.generateToken("rakesh@example.com");

        assertTrue(
                jwtService.isTokenValid(token)
        );
    }


    @Test
    void shouldReturnFalseForInvalidToken() {

        String invalidToken =
                "this.is.not.a.valid.jwt";

        assertFalse(
                jwtService.isTokenValid(invalidToken)
        );
    }


    @Test
    void shouldReturnFalseForTamperedToken() {

        String token =
                jwtService.generateToken("rakesh@example.com");

        String tamperedToken =
                token + "tampered";

        assertFalse(
                jwtService.isTokenValid(tamperedToken)
        );
    }
}