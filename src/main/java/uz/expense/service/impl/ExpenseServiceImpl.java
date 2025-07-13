package uz.expense.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import uz.expense.entity.Expense;
import uz.expense.exception.ExpenseBadException;
import uz.expense.mapper.ExpenseMapper;
import uz.expense.model.ExpenseRequest;
import uz.expense.repository.ExpenseRepository;
import uz.expense.service.ExpenseService;

import java.util.List;

@ApplicationScoped
public class ExpenseServiceImpl implements ExpenseService {

    @Inject
    ExpenseRepository expenseRepository;

    @Inject
    ExpenseMapper expenseMapper;


    @Override
    @Transactional
    public Expense saveExpense(ExpenseRequest request) {

        Expense expense = expenseMapper.toExpense(request);
        expenseRepository.persist(expense);
        return expense;
    }

    @Override
    public Expense findById(Long id) {

        Expense byId = expenseRepository.findById(id);

        if (byId == null) throw new ExpenseBadException("Expense with id: " + id + " not found");

        return byId;
    }

    @Override
    public List<Expense> findAll() {

        return List.of();
    }

    @Override
    public List<Expense> findByCategory(String category) {

        return List.of();
    }

    @Override
    public List<Expense> findByCategoryAndDate(Long id, String date) {

        return List.of();
    }

}
