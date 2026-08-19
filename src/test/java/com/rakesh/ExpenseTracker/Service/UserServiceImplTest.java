package com.rakesh.ExpenseTracker.Service;

import com.rakesh.ExpenseTracker.dto.UserRequestDTO;
import com.rakesh.ExpenseTracker.dto.UserResponseDTO;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.UserAlreadyExistsException;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import com.rakesh.ExpenseTracker.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final UserRepository userRepository =
            mock(UserRepository.class);

    private final PasswordEncoder passwordEncoder =
            mock(PasswordEncoder.class);

    private final UserServiceImpl userService =
            new UserServiceImpl(
                    userRepository,
                    passwordEncoder
            );
    @Test
    void shouldCheckBothEmailAndUsernameBeforeRegistration() {

        UserRequestDTO requestDTO = new UserRequestDTO();

        requestDTO.setUsername("rakesh");
        requestDTO.setEmail("rakesh@example.com");
        requestDTO.setPassword("password123");

        when(userRepository.existsByEmail("rakesh@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("rakesh"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setUsername("rakesh");
        savedUser.setEmail("rakesh@example.com");
        savedUser.setPassword("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDTO response =
                userService.registerUser(requestDTO);

        assertNotNull(response);

        assertEquals(
                "rakesh",
                response.getUsername()
        );

        verify(userRepository)
                .existsByEmail("rakesh@example.com");

        verify(userRepository)
                .existsByUsername("rakesh");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }
    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {

        UserRequestDTO requestDTO = new UserRequestDTO();

        requestDTO.setUsername("rakesh");
        requestDTO.setEmail("newemail@example.com");
        requestDTO.setPassword("password123");

        when(userRepository.existsByEmail("newemail@example.com"))
                .thenReturn(false);

        when(userRepository.existsByUsername("rakesh"))
                .thenReturn(true);

        UserAlreadyExistsException exception =
                assertThrows(
                        UserAlreadyExistsException.class,
                        () -> userService.registerUser(requestDTO)
                );

        assertEquals(
                "Username already exists: rakesh",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }
    @Test
    void shouldRegisterUserAndEncodePassword() {

        UserRequestDTO requestDTO = new UserRequestDTO();

        requestDTO.setUsername("rakesh");
        requestDTO.setEmail("rakesh@example.com");
        requestDTO.setPassword("password123");


        when(userRepository.existsByEmail("rakesh@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");


        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setUsername("rakesh");
        savedUser.setEmail("rakesh@example.com");
        savedUser.setPassword("hashedPassword");


        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);


        UserResponseDTO response =
                userService.registerUser(requestDTO);


        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "rakesh",
                response.getUsername()
        );

        assertEquals(
                "rakesh@example.com",
                response.getEmail()
        );


        verify(passwordEncoder)
                .encode("password123");


        verify(userRepository)
                .save(any(User.class));
    }


    @Test
    void shouldStoreEncodedPassword() {

        UserRequestDTO requestDTO = new UserRequestDTO();

        requestDTO.setUsername("rakesh");
        requestDTO.setEmail("rakesh@example.com");
        requestDTO.setPassword("password123");


        when(userRepository.existsByEmail("rakesh@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("hashedPassword");


        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {

                    User user = invocation.getArgument(0);

                    assertEquals(
                            "hashedPassword",
                            user.getPassword()
                    );

                    return user;
                });


        userService.registerUser(requestDTO);


        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }


    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        UserRequestDTO requestDTO = new UserRequestDTO();

        requestDTO.setUsername("rakesh");
        requestDTO.setEmail("rakesh@example.com");
        requestDTO.setPassword("password123");


        when(userRepository.existsByEmail("rakesh@example.com"))
                .thenReturn(true);


        UserAlreadyExistsException exception =
                assertThrows(
                        UserAlreadyExistsException.class,
                        () -> userService.registerUser(requestDTO)
                );


        assertEquals(
                "User already exists with email: rakesh@example.com",
                exception.getMessage()
        );


        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }
}