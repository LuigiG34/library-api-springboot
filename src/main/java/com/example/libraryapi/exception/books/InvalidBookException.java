package com.example.libraryapi.exception.books;

public class InvalidBookException extends IllegalArgumentException {
    public InvalidBookException(String message) {
        super(message);
    }
}
