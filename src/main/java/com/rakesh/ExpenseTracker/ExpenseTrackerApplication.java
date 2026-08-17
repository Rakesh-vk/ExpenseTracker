package com.rakesh.ExpenseTracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ExpenseTrackerApplication {

	public static void main(String[] args) {
        log.info("Starting the Expense Tracker App");
        SpringApplication.run(ExpenseTrackerApplication.class, args);
	}

}
