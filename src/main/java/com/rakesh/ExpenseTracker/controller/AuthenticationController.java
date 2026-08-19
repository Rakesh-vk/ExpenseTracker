package com.rakesh.ExpenseTracker.controller;

import com.rakesh.ExpenseTracker.dto.LoginRequestDTO;
import com.rakesh.ExpenseTracker.dto.LoginResponseDTO;
import com.rakesh.ExpenseTracker.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(
        name = "Authentication",
        description = "APIs for user authentication"
)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationService authenticationService) {

        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login successful"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid login request"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Invalid email or password"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        LoginResponseDTO response =
                authenticationService.login(loginRequestDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}