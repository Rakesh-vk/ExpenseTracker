package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.entity.Expense;

import java.util.List;

public interface expenseService {
    public List<Expense> getAllData();
    public Expense saveData(Expense newData);
}
