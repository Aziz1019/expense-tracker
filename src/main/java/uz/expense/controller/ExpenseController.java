package uz.expense.controller;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import uz.expense.entity.Expense;
import uz.expense.entity.ExpenseCategory;
import uz.expense.model.ExpenseRequest;
import uz.expense.service.ExpenseService;
import uz.expense.service.FileService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Path("/exp")
public class ExpenseController {

    @Inject
    ExpenseService expenseService;

    @Inject
    FileService fileService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public StringBuilder hello() {

        ExpenseRequest expense = new ExpenseRequest();
        expense.setDate(LocalDate.now());
        expense.setAmount(new BigDecimal("20000"));
        expense.setCategory(ExpenseCategory.ENTERTAINMENT);
        expense.setDescription("This is a description saved on database and written on a file");

        Expense expenseEntity = expenseService.saveExpense(expense);

        fileService.writeToFile(expenseEntity);
        Log.info("Successfully Written file " + expenseEntity.toCsv());

        // Reading from file
        String allDataFromFile = fileService.readFromFile();

        // Reading from file by its id
        String fileById = fileService.readFromFileById(expenseEntity.getId());

        StringBuilder builder = new StringBuilder();

        builder.append("\n");
        builder.append("The Written File: ").append(expenseEntity.toCsv());
        builder.append("\n");
        builder.append("\n");
        builder.append("File by id: ").append(fileById);
        builder.append("\n");
        builder.append("\n");
        builder.append(allDataFromFile);

        return builder;
    }
}
