package com.rakesh.ExpenseTracker.Service;

import com.rakesh.ExpenseTracker.dto.ExpenseRequestDTO;
import com.rakesh.ExpenseTracker.dto.ExpenseResponseDTO;
import com.rakesh.ExpenseTracker.entity.Expense;
import com.rakesh.ExpenseTracker.entity.User;
import com.rakesh.ExpenseTracker.exception.ExpenseNotFound;
import com.rakesh.ExpenseTracker.repository.ExpenseRepository;
import com.rakesh.ExpenseTracker.repository.UserRepository;
import com.rakesh.ExpenseTracker.service.ExpenseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User user;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setUsername("rakesh");
        user.setEmail("rakesh@example.com");
        user.setPassword("hashedPassword");

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "rakesh@example.com",
                                null
                        )
                );

        when(userRepository.findByEmail("rakesh@example.com"))
                .thenReturn(Optional.of(user));
    }


    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // GET EXPENSE BY ID
    // =========================================================

    @Test
    void shouldGetExpenseById() {

        Expense expense = new Expense();

        expense.setId(1L);
        expense.setSpendOn("Food");
        expense.setAmount(new BigDecimal("500"));
        expense.setUser(user);

        when(expenseRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(expense));


        ExpenseResponseDTO response =
                expenseService.getDataById(1L);


        assertEquals(1L, response.getId());

        assertEquals(
                "Food",
                response.getSpendOn()
        );

        assertEquals(
                new BigDecimal("500"),
                response.getAmount()
        );


        verify(expenseRepository)
                .findByIdAndUser(1L, user);
    }


    // =========================================================
    // GET EXPENSE - NOT FOUND
    // =========================================================

    @Test
    void shouldThrowExceptionWhenExpenseDoesNotExist() {

        when(expenseRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());


        ExpenseNotFound exception =
                assertThrows(
                        ExpenseNotFound.class,
                        () -> expenseService.getDataById(99L)
                );


        assertEquals(
                "Expense not found with id 99",
                exception.getMessage()
        );


        verify(expenseRepository)
                .findByIdAndUser(99L, user);
    }


    // =========================================================
    // CREATE EXPENSE
    // =========================================================

    @Test
    void shouldCreateExpense() {

        ExpenseRequestDTO request =
                new ExpenseRequestDTO();

        request.setSpendOn("Food");
        request.setAmount(
                new BigDecimal("500")
        );


        Expense savedExpense =
                new Expense();

        savedExpense.setId(1L);
        savedExpense.setSpendOn("Food");
        savedExpense.setAmount(
                new BigDecimal("500")
        );
        savedExpense.setUser(user);


        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(savedExpense);


        ExpenseResponseDTO response =
                expenseService.saveData(request);


        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Food",
                response.getSpendOn()
        );

        assertEquals(
                new BigDecimal("500"),
                response.getAmount()
        );


        verify(expenseRepository)
                .save(any(Expense.class));
    }


    // =========================================================
    // UPDATE EXPENSE
    // =========================================================

    @Test
    void shouldUpdateExpense() {

        Expense existingExpense =
                new Expense();

        existingExpense.setId(1L);
        existingExpense.setSpendOn("Food");
        existingExpense.setAmount(
                new BigDecimal("500")
        );
        existingExpense.setUser(user);


        ExpenseRequestDTO request =
                new ExpenseRequestDTO();

        request.setSpendOn("Shopping");
        request.setAmount(
                new BigDecimal("1000")
        );


        when(expenseRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(existingExpense));

        when(expenseRepository.save(existingExpense))
                .thenReturn(existingExpense);


        ExpenseResponseDTO response =
                expenseService.updateData(
                        1L,
                        request
                );


        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Shopping",
                response.getSpendOn()
        );

        assertEquals(
                new BigDecimal("1000"),
                response.getAmount()
        );


        verify(expenseRepository)
                .findByIdAndUser(1L, user);

        verify(expenseRepository)
                .save(existingExpense);
    }


    // =========================================================
    // DELETE EXPENSE
    // =========================================================

    @Test
    void shouldDeleteExpense() {

        Expense expense =
                new Expense();

        expense.setId(1L);
        expense.setSpendOn("Food");
        expense.setAmount(
                new BigDecimal("500")
        );
        expense.setUser(user);


        when(expenseRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(expense));


        expenseService.deleteExpense(1L);


        verify(expenseRepository)
                .findByIdAndUser(1L, user);

        verify(expenseRepository)
                .delete(expense);
    }


    // =========================================================
    // UPDATE - NOT FOUND
    // =========================================================

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingExpense() {

        ExpenseRequestDTO request =
                new ExpenseRequestDTO();

        request.setSpendOn("Shopping");
        request.setAmount(
                new BigDecimal("1000")
        );


        when(expenseRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());


        ExpenseNotFound exception =
                assertThrows(
                        ExpenseNotFound.class,
                        () -> expenseService.updateData(
                                99L,
                                request
                        )
                );


        assertEquals(
                "Expense not found with id 99",
                exception.getMessage()
        );


        verify(expenseRepository)
                .findByIdAndUser(99L, user);

        verify(
                expenseRepository,
                never()
        ).save(any(Expense.class));
    }


    // =========================================================
    // DELETE - NOT FOUND
    // =========================================================

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingExpense() {

        when(expenseRepository.findByIdAndUser(99L, user))
                .thenReturn(Optional.empty());


        ExpenseNotFound exception =
                assertThrows(
                        ExpenseNotFound.class,
                        () -> expenseService.deleteExpense(99L)
                );


        assertEquals(
                "Expense not found with id 99",
                exception.getMessage()
        );


        verify(expenseRepository)
                .findByIdAndUser(99L, user);

        verify(
                expenseRepository,
                never()
        ).delete(any(Expense.class));
    }


    // =========================================================
    // GET ALL EXPENSES
    // =========================================================

    @Test
    void shouldGetAllExpenses() {

        Expense expense1 =
                new Expense();

        expense1.setId(1L);
        expense1.setSpendOn("Food");
        expense1.setAmount(
                new BigDecimal("500")
        );
        expense1.setDateAndTime(
                LocalDateTime.now()
        );
        expense1.setUser(user);


        Expense expense2 =
                new Expense();

        expense2.setId(2L);
        expense2.setSpendOn("Travel");
        expense2.setAmount(
                new BigDecimal("1000")
        );
        expense2.setDateAndTime(
                LocalDateTime.now()
        );
        expense2.setUser(user);


        when(expenseRepository.findAllByUser(user))
                .thenReturn(
                        List.of(
                                expense1,
                                expense2
                        )
                );


        List<ExpenseResponseDTO> result =
                expenseService.getAllData();


        assertEquals(
                2,
                result.size()
        );


        assertEquals(
                1L,
                result.get(0).getId()
        );

        assertEquals(
                "Food",
                result.get(0).getSpendOn()
        );

        assertEquals(
                new BigDecimal("500"),
                result.get(0).getAmount()
        );


        assertEquals(
                2L,
                result.get(1).getId()
        );

        assertEquals(
                "Travel",
                result.get(1).getSpendOn()
        );

        assertEquals(
                new BigDecimal("1000"),
                result.get(1).getAmount()
        );


        verify(expenseRepository)
                .findAllByUser(user);
    }


    // =========================================================
    // SECURITY - CANNOT ACCESS ANOTHER USER'S EXPENSE
    // =========================================================

    @Test
    void shouldNotAllowAccessToAnotherUsersExpense() {

        when(expenseRepository.findByIdAndUser(10L, user))
                .thenReturn(Optional.empty());


        ExpenseNotFound exception =
                assertThrows(
                        ExpenseNotFound.class,
                        () -> expenseService.getDataById(10L)
                );


        assertEquals(
                "Expense not found with id 10",
                exception.getMessage()
        );


        verify(expenseRepository)
                .findByIdAndUser(10L, user);
    }


    // =========================================================
    // SECURITY - CANNOT UPDATE ANOTHER USER'S EXPENSE
    // =========================================================

    @Test
    void shouldNotAllowUpdatingAnotherUsersExpense() {

        ExpenseRequestDTO request =
                new ExpenseRequestDTO();

        request.setSpendOn("Hacked Expense");
        request.setAmount(
                new BigDecimal("1")
        );


        when(expenseRepository.findByIdAndUser(10L, user))
                .thenReturn(Optional.empty());


        ExpenseNotFound exception =
                assertThrows(
                        ExpenseNotFound.class,
                        () -> expenseService.updateData(
                                10L,
                                request
                        )
                );


        assertEquals(
                "Expense not found with id 10",
                exception.getMessage()
        );


        verify(expenseRepository)
                .findByIdAndUser(10L, user);

        verify(
                expenseRepository,
                never()
        ).save(any(Expense.class));
    }


    // =========================================================
    // SECURITY - CANNOT DELETE ANOTHER USER'S EXPENSE
    // =========================================================

    @Test
    void shouldNotAllowDeletingAnotherUsersExpense() {

        when(expenseRepository.findByIdAndUser(10L, user))
                .thenReturn(Optional.empty());


        ExpenseNotFound exception =
                assertThrows(
                        ExpenseNotFound.class,
                        () -> expenseService.deleteExpense(10L)
                );


        assertEquals(
                "Expense not found with id 10",
                exception.getMessage()
        );


        verify(expenseRepository)
                .findByIdAndUser(10L, user);

        verify(
                expenseRepository,
                never()
        ).delete(any(Expense.class));
    }
}