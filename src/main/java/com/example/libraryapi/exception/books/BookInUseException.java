package com.example.libraryapi.exception.books;

public class BookInUseException extends IllegalStateException {
    public BookInUseException(String message) {
        super(message);
    }
}
