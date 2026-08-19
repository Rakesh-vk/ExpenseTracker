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

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        when(jwtService.isTokenValid(token))
                .thenReturn(true);

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

        assertNotNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        assertEquals(
                email,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        verify(jwtService)
                .extractEmail(token);

        verify(jwtService)
                .isTokenValid(token);

        verify(userDetailsService)
                .loadUserByUsername(email);

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

        when(jwtService.extractEmail(token))
                .thenThrow(new RuntimeException("Invalid token"));

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

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extractEmail(token);

        verifyNoInteractions(userDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }


    // =========================================================
    // VALID JWT BUT INVALIDATED
    // =========================================================

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws Exception {

        String token = "expired-token";
        String email = "rakesh@example.com";

        UserDetails userDetails =
                User.withUsername(email)
                        .password("hashedPassword")
                        .authorities("USER")
                        .build();

        when(jwtService.extractEmail(token))
                .thenReturn(email);

        when(jwtService.isTokenValid(token))
                .thenReturn(false);

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

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extractEmail(token);

        verify(jwtService)
                .isTokenValid(token);

        verify(filterChain)
                .doFilter(request, response);
    }
}