package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ExpenseResponseDTO> getAllData() {

        log.debug("Fetching all expenses");

        String email = getAuthenticatedUserEmail();

        User user = getAuthenticatedUser(email);

        List<ExpenseResponseDTO> expenses =
                expenseRepository.findAll()
                        .stream()
                        .filter(expense ->
                                expense.getUser()
                                        .getId()
                                        .equals(user.getId())
                        )
                        .map(expense -> new ExpenseResponseDTO(
                                expense.getId(),
                                expense.getSpendOn(),
                                expense.getAmount(),
                                expense.getDateAndTime()
                        ))
                        .toList();

        log.debug(
                "Fetched {} expenses for user={}",
                expenses.size(),
                email
        );

        return expenses;
    }

    @Override
    public ExpenseResponseDTO saveData(
            ExpenseRequestDTO requestDTO) {

        log.info("Creating new expense");

        String email = getAuthenticatedUserEmail();

        User user = getAuthenticatedUser(email);

        Expense expense = new Expense();

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());

        // Associate expense with authenticated user
        expense.setUser(user);

        Expense savedExpense =
                expenseRepository.save(expense);

        log.info(
                "Expense created successfully with id={} for user={}",
                savedExpense.getId(),
                email
        );

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

        String email = getAuthenticatedUserEmail();

        User user = getAuthenticatedUser(email);

        Expense expense =
                expenseRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Expense not found with id={}",
                                    id
                            );

                            return new ExpenseNotFound(
                                    "Expense not found with id " + id
                            );
                        });

        // Prevent user from modifying another user's expense
        if (!expense.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ExpenseNotFound(
                    "Expense not found with id " + id
            );
        }

        expense.setSpendOn(requestDTO.getSpendOn());
        expense.setAmount(requestDTO.getAmount());

        Expense updatedExpense =
                expenseRepository.save(expense);

        log.info(
                "Expense updated successfully with id={} for user={}",
                id,
                email
        );

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

        String email = getAuthenticatedUserEmail();

        User user = getAuthenticatedUser(email);

        Expense expense =
                expenseRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Expense not found with id={}",
                                    id
                            );

                            return new ExpenseNotFound(
                                    "Expense not found with id: " + id
                            );
                        });

        // Prevent user from accessing another user's expense
        if (!expense.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ExpenseNotFound(
                    "Expense not found with id: " + id
            );
        }

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

        String email = getAuthenticatedUserEmail();

        User user = getAuthenticatedUser(email);

        Expense expense =
                expenseRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn(
                                    "Expense not found with id={}",
                                    id
                            );

                            return new ExpenseNotFound(
                                    "Expense not found with id " + id
                            );
                        });

        // Prevent user from deleting another user's expense
        if (!expense.getUser()
                .getId()
                .equals(user.getId())) {

            throw new ExpenseNotFound(
                    "Expense not found with id " + id
            );
        }

        expenseRepository.delete(expense);

        log.info(
                "Expense deleted successfully with id={} for user={}",
                id,
                email
        );
    }

    // =========================================================
    // AUTHENTICATED USER HELPERS
    // =========================================================

    private String getAuthenticatedUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication.getName();
    }

    private User getAuthenticatedUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }
}