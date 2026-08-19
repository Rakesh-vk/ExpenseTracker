package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.UserRequestDTO;
import com.rakesh.ExpenseTracker.dto.UserResponseDTO;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.UserAlreadyExistsException;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {

        // 1. Check if email already exists
        boolean emailExists =
                userRepository.existsByEmail(userRequestDTO.getEmail());

        boolean usernameExists =
                userRepository.existsByUsername(userRequestDTO.getUsername());

        if (emailExists) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: "
                            + userRequestDTO.getEmail()
            );
        }

        if (usernameExists) {
            throw new UserAlreadyExistsException(
                    "Username already exists: "
                            + userRequestDTO.getUsername()
            );
        }

        // 2. Create User entity
        User user = new User();

        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());

        // 3. Hash password before storing
        user.setPassword(
                passwordEncoder.encode(userRequestDTO.getPassword())
        );

        // 4. Save user
        User savedUser = userRepository.save(user);

        // 5. Return response DTO
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getDateAndTime()
        );
    }
}
