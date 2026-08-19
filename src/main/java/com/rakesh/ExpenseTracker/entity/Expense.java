package com.rakesh.ExpenseTracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String spendOn;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "expense_date_time", nullable = false)
    private LocalDateTime dateAndTime;

    @PrePersist
    protected void onCreate() {
        dateAndTime = LocalDateTime.now();
    }
}