package com.rakesh.ExpenseTracker.security;

import com.rakesh.ExpenseTracker.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService =
            mock(JwtService.class);

    private final UserDetailsService userDetailsService =
            mock(UserDetailsService.class);

    private final FilterChain filterChain =
            mock(FilterChain.class);

    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(
                    jwtService,
                    userDetailsService
            );

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // VALID JWT
    // =========================================================

    @Test
    void shouldAuthenticateUserWithValidToken()
            throws Exception {

        String token = "valid-token";
        String email = "rakesh@example.com";

        UserDetails userDetails =
                User.withUsername(email)
                        .password("hashedPassword")
                        .authorities("USER")
                        .build();

        // Token must be valid first
        when(jwtService.isTokenValid(token))
                .thenReturn(true);

        // Then extract email
        when(jwtService.extractEmail(token))
                .thenReturn(email);

        // Then load user
        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        // Assert authentication exists
        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        // Assert authenticated username
        assertEquals(
                email,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        // Verify JWT validation
        verify(jwtService)
                .isTokenValid(token);

        // Verify email extraction
        verify(jwtService)
                .extractEmail(token);

        // Verify user lookup
        verify(userDetailsService)
                .loadUserByUsername(email);

        // Verify filter chain continues
        verify(filterChain)
                .doFilter(request, response);
    }


    // =========================================================
    // NO AUTHORIZATION HEADER
    // =========================================================

    @Test
    void shouldContinueWhenAuthorizationHeaderMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }


    // =========================================================
    // INVALID AUTHORIZATION HEADER
    // =========================================================

    @Test
    void shouldContinueWhenAuthorizationHeaderIsInvalid()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Basic abc123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }


    // =========================================================
    // INVALID JWT
    // =========================================================

    @Test
    void shouldContinueWhenJwtIsInvalid()
            throws Exception {

        String token = "invalid-token";

        // Invalid JWT
        when(jwtService.isTokenValid(token))
                .thenReturn(false);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        // User must NOT be authenticated
        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        // Token validation should happen
        verify(jwtService)
                .isTokenValid(token);

        // Email should NOT be extracted
        verify(jwtService, never())
                .extractEmail(anyString());

        // User should NOT be loaded
        verifyNoInteractions(userDetailsService);

        // Request should continue
        verify(filterChain)
                .doFilter(request, response);
    }


    // =========================================================
    // EXPIRED / INVALID JWT
    // =========================================================

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws Exception {

        String token = "expired-token";

        // Expired/invalid token
        when(jwtService.isTokenValid(token))
                .thenReturn(false);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        // Authentication must remain null
        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        // Token validation must happen
        verify(jwtService)
                .isTokenValid(token);

        // Do NOT extract email from invalid token
        verify(jwtService, never())
                .extractEmail(anyString());

        // Do NOT load user
        verifyNoInteractions(userDetailsService);

        // Continue filter chain
        verify(filterChain)
                .doFilter(request, response);
    }
}