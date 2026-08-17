package com.rakesh.ExpenseTracker.service;

import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class expenseServiceImpl implements expenseService{
    @Autowired
    private ExpenseRepository expenseRepository;
    @Override
    public List<Expense> getAllData() {
        log.debug("Entered getAllData service function");
        return expenseRepository.findAll();
    }

    @Override
    public Expense saveData(Expense newData) {
        log.debug("Entered saveData service function");
        Expense record= new Expense();
        record.setId(newData.getId());
        record.setAmount(newData.getAmount());
        record.setSpendOn(newData.getSpendOn());
        expenseRepository.save(record);
        log.debug("new expense is added");
        return record;
    }
}
