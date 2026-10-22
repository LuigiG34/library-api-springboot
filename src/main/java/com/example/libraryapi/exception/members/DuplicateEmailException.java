package com.example.libraryapi.exception.members;

public class DuplicateEmailException extends IllegalStateException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}