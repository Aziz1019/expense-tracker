package uz.expense.exception;

import lombok.Data;

@Data
public class ExpenseException extends RuntimeException {

    private final Integer code;
    private final String message;
    private final String devMessage;

}
