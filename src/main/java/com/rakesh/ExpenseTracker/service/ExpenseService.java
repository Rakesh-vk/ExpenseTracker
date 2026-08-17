package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    public List<ExpenseResponseDTO> getAllData();
    public ExpenseResponseDTO saveData(ExpenseRequestDTO requestDTO);

    ExpenseResponseDTO updateData(ExpenseRequestDTO requestDTO);

    ExpenseResponseDTO getDataById(ExpenseRequestDTO requestDTO);
}
