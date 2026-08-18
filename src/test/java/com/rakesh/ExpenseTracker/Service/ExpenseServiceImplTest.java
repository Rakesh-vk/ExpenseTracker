package com.rakesh.ExpenseTracker.Service;

import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import com.rakesh.ExpenseTracker.service.ExpenseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void shouldGetExpenseById() {
        log.debug("findById is tested");
        // Arrange
        Expense expense = new Expense();

        expense.setId(1L);
        expense.setSpendOn("Food");
        expense.setAmount(new BigDecimal("500"));

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        // Act
        ExpenseResponseDTO response =
                expenseService.getDataById(1L);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Food", response.getSpendOn());
        assertEquals(
                new BigDecimal("500"),
                response.getAmount()
        );

        // Verify
        verify(expenseRepository).findById(1L);

    }
    @Test
    void shouldThrowExceptionWhenExpenseDoesNotExist() {
        log.debug("findById is tested with invalid id ");

        when(expenseRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExpenseNotFound.class,
                () -> expenseService.getDataById(99L)
        );

        verify(expenseRepository).findById(99L);

    }
    @Test
    void shouldCreateExpense() {

        // Arrange
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setSpendOn("Food");
        request.setAmount(new BigDecimal("500"));

        Expense savedExpense = new Expense();
        savedExpense.setId(1L);
        savedExpense.setSpendOn("Food");
        savedExpense.setAmount(new BigDecimal("500"));

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(savedExpense);

        // Act
        ExpenseResponseDTO response =
                expenseService.saveData(request);

        log.info("Create Expense Test");
        log.info("Request: spendOn={}, amount={}",
                request.getSpendOn(),
                request.getAmount());

        log.info("Response: id={}, spendOn={}, amount={}",
                response.getId(),
                response.getSpendOn(),
                response.getAmount());

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Food", response.getSpendOn());
        assertEquals(
                new BigDecimal("500"),
                response.getAmount()
        );

        verify(expenseRepository).save(any(Expense.class));
    }
    @Test
    void shouldUpdateExpense() {

        // Arrange
        Expense existingExpense = new Expense();
        existingExpense.setId(1L);
        existingExpense.setSpendOn("Food");
        existingExpense.setAmount(new BigDecimal("500"));

        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setSpendOn("Shopping");
        request.setAmount(new BigDecimal("1000"));

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(existingExpense));

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(existingExpense);

        // Act
        ExpenseResponseDTO response =
                expenseService.updateData(1L, request);

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Shopping", response.getSpendOn());
        assertEquals(
                new BigDecimal("1000"),
                response.getAmount()
        );

        // Verify
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(existingExpense);

        log.info(
                "Updated expense: id={}, spendOn={}, amount={}",
                response.getId(),
                response.getSpendOn(),
                response.getAmount()
        );
    }
    @Test
    void shouldDeleteExpense() {

        // Arrange
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setSpendOn("Food");
        expense.setAmount(new BigDecimal("500"));

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        // Act
        expenseService.deleteExpense(1L);

        // Verify
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).delete(expense);

        log.info(
                "Deleted expense: id={}, spendOn={}, amount={}",
                expense.getId(),
                expense.getSpendOn(),
                expense.getAmount()
        );
    }
    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingExpense() {

        // Arrange
        ExpenseRequestDTO request = new ExpenseRequestDTO();
        request.setSpendOn("Shopping");
        request.setAmount(new BigDecimal("1000"));

        when(expenseRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        ExpenseNotFound exception = assertThrows(
                ExpenseNotFound.class,
                () -> expenseService.updateData(99L, request)
        );

        assertEquals(
                "Expense not found with id 99",
                exception.getMessage()
        );

        // Verify
        verify(expenseRepository).findById(99L);
        verify(expenseRepository, never())
                .save(any(Expense.class));

        log.info(
                "Update failed as expected: {}",
                exception.getMessage()
        );
    }
    @Test
    void shouldThrowExceptionWhenDeletingNonExistingExpense() {

        // Arrange
        when(expenseRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        ExpenseNotFound exception = assertThrows(
                ExpenseNotFound.class,
                () -> expenseService.deleteExpense(99L)
        );

        assertEquals(
                "Expense not found with id 99",
                exception.getMessage()
        );

        // Verify
        verify(expenseRepository).findById(99L);
        verify(expenseRepository, never())
                .delete(any(Expense.class));

        log.info(
                "Delete failed as expected: {}",
                exception.getMessage()
        );
    }
}