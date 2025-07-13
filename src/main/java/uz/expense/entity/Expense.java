package uz.expense.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    public String toCsv() {
        return String.join(",",
                String.valueOf(id),
                date.toString(),
                category.toString(),
                amount.toPlainString(),
                description
        );
    }


}
