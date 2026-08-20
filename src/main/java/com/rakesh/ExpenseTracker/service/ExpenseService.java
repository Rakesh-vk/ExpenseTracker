package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {
    public Page<ExpenseResponseDTO> getAllData(int page, int size);
    public ExpenseResponseDTO saveData(ExpenseRequestDTO requestDTO);

    ExpenseResponseDTO updateData(Long id, ExpenseRequestDTO requestDTO);

    ExpenseResponseDTO getDataById(Long id);
    void deleteExpense(Long id);
}
