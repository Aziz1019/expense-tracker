package uz.expense.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import uz.expense.entity.Expense;

@ApplicationScoped
public class ExpenseRepository implements PanacheRepositoryBase<Expense,Long> {

}
