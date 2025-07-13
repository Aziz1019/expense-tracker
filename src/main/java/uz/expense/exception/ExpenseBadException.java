package uz.expense.exception;

import jakarta.ws.rs.core.Response;

public class ExpenseBadException extends ExpenseException {


    public ExpenseBadException(String message) {

        super(Response.Status.BAD_REQUEST.getStatusCode(), message, "");
    }

    public ExpenseBadException(String message, String devMessage) {

        super(Response.Status.BAD_REQUEST.getStatusCode(), message, devMessage);
    }

}
