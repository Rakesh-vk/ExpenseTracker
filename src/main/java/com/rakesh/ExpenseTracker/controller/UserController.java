package com.rakesh.ExpenseTracker.controller;

import com.rakesh.ExpenseTracker.dto.ErrorResponseDTO;
import com.rakesh.ExpenseTracker.dto.UserRequestDTO;
import com.rakesh.ExpenseTracker.dto.UserResponseDTO;
import com.rakesh.ExpenseTracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(
        name = "User Management",
        description = "APIs for managing users"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account using username, email and password",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UserRequestDTO.class
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = UserResponseDTO.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid user data",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseDTO.class
                    )
            )
    )
    @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = ErrorResponseDTO.class
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> saveUser(
            @Valid @RequestBody UserRequestDTO userRequestDTO) {

        log.info("POST /user/register - registering new user");

        UserResponseDTO userResponseDTO =
                userService.registerUser(userRequestDTO);

        return new ResponseEntity<>(
                userResponseDTO,
                HttpStatus.CREATED
        );
    }
}