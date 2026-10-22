package com.example.libraryapi.exception.books;

public class InvalidSortByException extends IllegalArgumentException {
    public InvalidSortByException(String message) {
        super(message);
    }
}