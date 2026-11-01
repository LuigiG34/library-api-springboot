package com.example.libraryapi.exception.books;

import com.example.libraryapi.exception.parent.ConflictException;

public class DuplicateIsbnException extends ConflictException {
    public DuplicateIsbnException(String message) {
        super(message);
    }
}
