package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.LoginRequestDTO;
import com.rakesh.ExpenseTracker.dto.LoginResponseDTO;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.InvalidCredentialsException;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

        User user = userRepository
                .findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        boolean passwordMatches =
                passwordEncoder.matches(
                        loginRequestDTO.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        return new LoginResponseDTO(
                "Login successful",
                null
        );
    }
}