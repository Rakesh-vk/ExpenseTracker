package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        log.debug("getAllData running");
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
        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getSpendOn(),
                expense.getAmount(),
                expense.getDateAndTime()
        );
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


}
