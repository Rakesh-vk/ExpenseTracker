package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Override
    public List<ExpenseResponseDTO> getAllData() {
        return expenseRepository.findAll()
                .stream()
                .map(expense -> new ExpenseResponseDTO(
                        expense.getId(),
                        expense.getSpendOn(),
                        expense.getAmount(),
                        expense.getDateAndTime()
                ))
                .toList();
    }

    @Override
    public ExpenseResponseDTO saveData(ExpenseRequestDTO requestDTO) {

        Expense expense = new Expense();

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());
        expense.setDateAndTime(LocalDateTime.now());

        Expense savedExpense = expenseRepository.save(expense);

        return new ExpenseResponseDTO(
                savedExpense.getId(),
                savedExpense.getSpendOn(),
                savedExpense.getAmount(),
                savedExpense.getDateAndTime()
        );
    }

    @Override
    public ExpenseResponseDTO updateData(ExpenseRequestDTO requestDTO) {

        return null;
    }

    @Override
    public ExpenseResponseDTO getDataById(ExpenseRequestDTO requestDTO) {
        Long id= requestDTO.getId();

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        ExpenseResponseDTO responseDTO = new ExpenseResponseDTO();

        responseDTO.setId(expense.getId());
        responseDTO.setSpendOn(expense.getSpendOn());
        responseDTO.setAmount(expense.getAmount());
        responseDTO.setDateAndTime(expense.getDateAndTime());

        return responseDTO;
    }
}
