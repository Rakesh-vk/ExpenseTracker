package com.rakesh.ExpenseTracker.security;

import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private final UserRepository userRepository =
            mock(UserRepository.class);

    private final UserDetailsServiceImpl userDetailsService =
            new UserDetailsServiceImpl(userRepository);


    // =========================================================
    // USER FOUND
    // =========================================================

    @Test
    void shouldLoadUserByEmail() {

        User user = new User();

        user.setId(1L);
        user.setUsername("rakesh");
        user.setEmail("rakesh@example.com");
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail("rakesh@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        "rakesh@example.com"
                );

        assertNotNull(userDetails);

        assertEquals(
                "rakesh@example.com",
                userDetails.getUsername()
        );

        assertEquals(
                "hashedPassword",
                userDetails.getPassword()
        );

        assertEquals(
                1,
                userDetails.getAuthorities().size()
        );

        assertEquals(
                "USER",
                userDetails.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        verify(userRepository)
                .findByEmail("rakesh@example.com");
    }


    // =========================================================
    // USER NOT FOUND
    // =========================================================

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> userDetailsService.loadUserByUsername(
                                "unknown@example.com"
                        )
                );

        assertEquals(
                "User not found with email: unknown@example.com",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@example.com");
    }
}