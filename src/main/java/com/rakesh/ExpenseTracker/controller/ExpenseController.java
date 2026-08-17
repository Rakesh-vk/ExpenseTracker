package com.rakesh.ExpenseTracker.controller;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/Expense")
public class ExpenseController {


    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(){
        log.debug("getAllExpanses Controller entered");
        System.out.println("get Mapping");
        return new ResponseEntity<>(expenseService.getAllData(), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> saveExpense(
            @RequestBody ExpenseRequestDTO requestDTO) {

        ExpenseResponseDTO responseDTO =
                expenseService.saveData(requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
