package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;

import java.util.List;

public interface ExpenseService {
    public List<ExpenseResponseDTO> getAllData();
    public ExpenseResponseDTO saveData(ExpenseRequestDTO requestDTO);
}
