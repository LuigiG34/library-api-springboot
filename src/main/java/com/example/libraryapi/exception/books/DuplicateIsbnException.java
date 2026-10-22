package com.example.libraryapi.exception.books;

public class DuplicateIsbnException extends IllegalStateException {
    public DuplicateIsbnException(String message) {
        super(message);
    }
}
