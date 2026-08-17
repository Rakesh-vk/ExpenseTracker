package com.rakesh.ExpenseTracker.repository;

import com.rakesh.ExpenseTracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
}
