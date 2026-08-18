package com.rakesh.ExpenseTracker.repository;

import com.rakesh.ExpenseTracker.entity.Expense;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    void shouldSaveExpense() {

        Expense expense = new Expense();

        expense.setSpendOn("Food");
        expense.setAmount(new BigDecimal("500"));

        Expense savedExpense =
                expenseRepository.save(expense);

        assertNotNull(savedExpense.getId());

        assertEquals(
                "Food",
                savedExpense.getSpendOn()
        );

        assertEquals(
                new BigDecimal("500"),
                savedExpense.getAmount()
        );
    }

    @Test
    void shouldFindExpenseById() {

        Expense expense = new Expense();

        expense.setSpendOn("Travel");
        expense.setAmount(new BigDecimal("1000"));

        Expense savedExpense =
                expenseRepository.save(expense);

        Optional<Expense> result =
                expenseRepository.findById(savedExpense.getId());

        assertTrue(result.isPresent());

        Expense foundExpense = result.get();

        assertEquals(
                savedExpense.getId(),
                foundExpense.getId()
        );

        assertEquals(
                "Travel",
                foundExpense.getSpendOn()
        );

        assertEquals(
                new BigDecimal("1000"),
                foundExpense.getAmount()
        );
    }

    @Test
    void shouldReturnEmptyWhenExpenseDoesNotExist() {

        Optional<Expense> result =
                expenseRepository.findById(999999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteExpense() {

        Expense expense = new Expense();

        expense.setSpendOn("Shopping");
        expense.setAmount(new BigDecimal("2000"));

        Expense savedExpense =
                expenseRepository.save(expense);

        Long id = savedExpense.getId();

        expenseRepository.delete(savedExpense);

        Optional<Expense> result =
                expenseRepository.findById(id);

        assertTrue(result.isEmpty());
    }
}