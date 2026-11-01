package com.example.libraryapi.exception.members;

import com.example.libraryapi.exception.parent.ConflictException;

public class DuplicateEmailException extends ConflictException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}