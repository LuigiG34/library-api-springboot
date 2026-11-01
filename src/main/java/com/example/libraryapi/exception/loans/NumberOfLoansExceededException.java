package com.example.libraryapi.exception.loans;

import com.example.libraryapi.exception.parent.ConflictException;

public class NumberOfLoansExceededException extends ConflictException {
    public NumberOfLoansExceededException(String message) {
        super(message);
    }
}
