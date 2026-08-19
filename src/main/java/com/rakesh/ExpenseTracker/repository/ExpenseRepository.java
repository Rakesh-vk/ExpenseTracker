package com.rakesh.ExpenseTracker.repository;

import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    List<Expense> findAllByUser(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);
}