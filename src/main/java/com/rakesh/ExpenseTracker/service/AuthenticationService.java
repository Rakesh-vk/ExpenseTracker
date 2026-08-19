package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.LoginRequestDTO;
import com.rakesh.ExpenseTracker.dto.LoginResponseDTO;

public interface AuthenticationService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
}