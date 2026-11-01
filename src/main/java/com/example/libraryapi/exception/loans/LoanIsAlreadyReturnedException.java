package com.example.libraryapi.exception.loans;

import com.example.libraryapi.exception.parent.ConflictException;

public class LoanIsAlreadyReturnedException  extends ConflictException {
    public LoanIsAlreadyReturnedException(String message) {
        super(message);
    }
}
