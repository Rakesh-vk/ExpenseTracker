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

        log.debug("Fetching all expenses");

        List<ExpenseResponseDTO> expenses = expenseRepository.findAll()
                .stream()
                .map(expense -> new ExpenseResponseDTO(
                        expense.getId(),
                        expense.getSpendOn(),
                        expense.getAmount(),
                        expense.getDateAndTime()
                ))
                .toList();

        log.debug("Fetched {} expenses", expenses.size());

        return expenses;
    }

    @Override
    public ExpenseResponseDTO saveData(ExpenseRequestDTO requestDTO) {

        log.info("Creating new expense");
        Expense expense = new Expense();

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());

        Expense savedExpense = expenseRepository.save(expense);

        log.info("Expense created successfully with id={}",
                savedExpense.getId());

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

        log.info("Updating expense with id={}", id);

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Expense not found with id={}", id);

                    return new ExpenseNotFound(
                            "Expense not found with id " + id
                    );
                });

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());

        Expense updatedExpense = expenseRepository.save(expense);

        log.info("Expense updated successfully with id={}", id);

        return new ExpenseResponseDTO(
                updatedExpense.getId(),
                updatedExpense.getSpendOn(),
                updatedExpense.getAmount(),
                updatedExpense.getDateAndTime()
        );
    }

    @Override
    public ExpenseResponseDTO getDataById(Long id) {

        log.debug("Fetching expense with id={}", id);

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Expense not found with id={}", id);

                    return new ExpenseNotFound(
                            "Expense not found with id: " + id
                    );
                });

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getSpendOn(),
                expense.getAmount(),
                expense.getDateAndTime()
        );
    }

    @Override
    public void deleteExpense(Long id) {

        log.info("Deleting expense with id={}", id);

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Expense not found with id={}", id);

                    return new ExpenseNotFound(
                            "Expense not found with id " + id
                    );
                });

        expenseRepository.delete(expense);

        log.info("Expense deleted successfully with id={}", id);
    }
}