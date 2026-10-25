package com.example.libraryapi.exception.loans;

public class NumberOfLoansExceededException extends RuntimeException {
    public NumberOfLoansExceededException(String message) {
        super(message);
    }
}
