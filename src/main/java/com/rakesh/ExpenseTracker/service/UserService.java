package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.UserRequestDTO;
import com.rakesh.ExpenseTracker.dto.UserResponseDTO;


public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);
}
