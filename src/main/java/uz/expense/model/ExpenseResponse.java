package uz.expense.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import uz.expense.entity.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResponse {

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    private BigDecimal amount;

    private String description;

}
