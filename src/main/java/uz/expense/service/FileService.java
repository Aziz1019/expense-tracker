package uz.expense.service;

import uz.expense.entity.Expense;

public interface FileService {

    void writeToFile(Expense expense);

}
