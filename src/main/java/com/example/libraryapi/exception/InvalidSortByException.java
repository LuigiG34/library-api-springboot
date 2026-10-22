package com.example.libraryapi.exception;

public class InvalidSortByException extends IllegalArgumentException {
    public InvalidSortByException(String message) {
        super(message);
    }
}