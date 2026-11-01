package com.example.libraryapi.exception.books;

import com.example.libraryapi.exception.parent.ConflictException;

public class BookInUseException extends ConflictException {
    public BookInUseException(String message) {
        super(message);
    }
}
