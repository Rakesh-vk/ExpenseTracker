package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.exception.InvalidAmountException;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

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

        validateAmount(requestDTO.getAmount());

        Expense expense = new Expense();

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());

        Expense savedExpense = expenseRepository.save(expense);

        return new ExpenseResponseDTO(
                savedExpense.getId(),
                savedExpense.getSpendOn(),
                savedExpense.getAmount(),
                savedExpense.getDateAndTime()
        );
    }

    @Override
    public ExpenseResponseDTO updateData(
            Long id,
            ExpenseRequestDTO requestDTO) {
        validateAmount(requestDTO.getAmount());


        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ExpenseNotFound(
                                "Expense not found with id " + id
                        ));

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());

        Expense updatedExpense = expenseRepository.save(expense);

        return new ExpenseResponseDTO(
                updatedExpense.getId(),
                updatedExpense.getSpendOn(),
                updatedExpense.getAmount(),
                updatedExpense.getDateAndTime()
        );
    }

    @Override
    public ExpenseResponseDTO getDataById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFound("Expense not found with id: " + id));
        ExpenseResponseDTO responseDTO = new ExpenseResponseDTO();
        responseDTO.setId(expense.getId());
        responseDTO.setSpendOn(expense.getSpendOn());
        responseDTO.setAmount(expense.getAmount());

        return responseDTO;
    }

    @Override
    public void deleteExpense(Long id) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ExpenseNotFound(
                                "Expense not found with id " + id
                        ));

        expenseRepository.delete(expense);
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Amount must be greater than zero"
            );
        }
    }
}
