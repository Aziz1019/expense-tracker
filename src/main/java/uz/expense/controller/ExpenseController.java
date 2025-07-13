package uz.expense.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import uz.expense.entity.Expense;
import uz.expense.entity.ExpenseCategory;
import uz.expense.model.ExpenseRequest;
import uz.expense.service.ExpenseService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Path("/exp")
public class ExpenseController {

    @Inject
    ExpenseService expenseService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        ExpenseRequest expense = new ExpenseRequest();
        expense.setDate(LocalDate.now());
        expense.setAmount(new BigDecimal("15000"));
        expense.setCategory(ExpenseCategory.FOOD);
        expense.setDescription("This is a test Description");

        expenseService.saveExpense(expense);

        return "Hello from Quarkus REST";
    }


}
