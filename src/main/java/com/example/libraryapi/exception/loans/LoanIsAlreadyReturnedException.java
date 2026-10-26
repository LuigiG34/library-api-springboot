package com.example.libraryapi.exception.loans;

public class LoanIsAlreadyReturnedException  extends RuntimeException {
    public LoanIsAlreadyReturnedException(String message) {
        super(message);
    }
}
