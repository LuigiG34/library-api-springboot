package com.example.libraryapi.exception;

public class BookInUseException extends IllegalStateException {
    public BookInUseException(String message) {
        super(message);
    }
}
