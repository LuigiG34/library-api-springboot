package com.example.libraryapi.exception;

public class DuplicateIsbnException extends IllegalStateException {
    public DuplicateIsbnException(String message) {
        super(message);
    }
}
