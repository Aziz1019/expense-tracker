package uz.expense.service;

import uz.expense.entity.Expense;
import uz.expense.model.ExpenseRequest;

import java.util.List;

public interface ExpenseService {

    Expense saveExpense(ExpenseRequest request);

    Expense findById(Long id);

    List<Expense> findAll();

    List<Expense> findByCategory(String category);

    List<Expense> findByCategoryAndDate(Long id, String date);


}
