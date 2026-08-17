package com.rakesh.ExpenseTracker.controller;

import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.service.expenseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/Expense")
public class expenseController {


    @Autowired
    private expenseServiceImpl expenseServiceImpls;

    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses(){
        log.debug("getAllExpanses Controller entered");
        System.out.println("get Mapping");
        return new ResponseEntity<>(expenseServiceImpls.getAllData(), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Expense> saveExpense(@RequestBody Expense expense){
        log.debug("SaveExpense Controller entered");
        Expense saved =expenseServiceImpls.saveData(expense);
        log.debug("expense is saved");
        return new ResponseEntity<>(saved,HttpStatus.CREATED);
    }
}
