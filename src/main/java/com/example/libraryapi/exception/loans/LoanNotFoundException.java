package com.example.libraryapi.exception.loans;

public class LoanNotFoundException  extends RuntimeException {
    public LoanNotFoundException(String message) {
        super(message);
    }
}
