package com.rakesh.ExpenseTracker.repository;

import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findAllByUser(
            User user,
            Pageable pageable
    );

    Optional<Expense> findByIdAndUser(
            Long id,
            User user
    );
}