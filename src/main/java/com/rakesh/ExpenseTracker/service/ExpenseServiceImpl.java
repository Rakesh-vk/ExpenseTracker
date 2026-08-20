package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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


    // =========================================================
    // GET CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }


    // =========================================================
    // GET ALL EXPENSES
    // =========================================================

    @Override
    public Page<ExpenseResponseDTO> getAllData(int page, int size) {

        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        return expenseRepository
                .findAllByUserOrderByDateAndTimeDesc(
                        currentUser,
                        pageable
                )
                .map(this::mapToResponseDTO);
    }


    // =========================================================
    // GET EXPENSE BY ID
    // =========================================================

    @Override
    public ExpenseResponseDTO getDataById(Long id) {

        User currentUser = getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ExpenseNotFound(
                                        "Expense not found with id " + id
                                )
                        );

        return mapToResponseDTO(expense);
    }


    // =========================================================
    // CREATE EXPENSE
    // =========================================================

    @Override
    public ExpenseResponseDTO saveData(
            ExpenseRequestDTO requestDTO) {

        User currentUser = getCurrentUser();

        Expense expense = new Expense();

        expense.setSpendOn(
                requestDTO.getSpendOn()
        );

        expense.setAmount(
                requestDTO.getAmount()
        );

        expense.setUser(currentUser);

        Expense savedExpense =
                expenseRepository.save(expense);

        return mapToResponseDTO(savedExpense);
    }


    // =========================================================
    // UPDATE EXPENSE
    // =========================================================

    @Override
    public ExpenseResponseDTO updateData(
            Long id,
            ExpenseRequestDTO requestDTO) {

        User currentUser = getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ExpenseNotFound(
                                        "Expense not found with id " + id
                                )
                        );

        expense.setSpendOn(
                requestDTO.getSpendOn()
        );

        expense.setAmount(
                requestDTO.getAmount()
        );

        Expense updatedExpense =
                expenseRepository.save(expense);

        return mapToResponseDTO(updatedExpense);
    }


    // =========================================================
    // DELETE EXPENSE
    // =========================================================

    @Override
    public void deleteExpense(Long id) {

        User currentUser = getCurrentUser();

        Expense expense =
                expenseRepository
                        .findByIdAndUser(id, currentUser)
                        .orElseThrow(() ->
                                new ExpenseNotFound(
                                        "Expense not found with id " + id
                                )
                        );

        expenseRepository.delete(expense);
    }


    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private ExpenseResponseDTO mapToResponseDTO(
            Expense expense) {

        ExpenseResponseDTO responseDTO =
                new ExpenseResponseDTO();

        responseDTO.setId(
                expense.getId()
        );

        responseDTO.setSpendOn(
                expense.getSpendOn()
        );

        responseDTO.setAmount(
                expense.getAmount()
        );

        responseDTO.setDateAndTime(
                expense.getDateAndTime()
        );

        return responseDTO;
    }
}