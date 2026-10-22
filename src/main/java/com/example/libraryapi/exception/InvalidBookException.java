package com.example.libraryapi.exception;

public class InvalidBookException extends IllegalArgumentException {
    public InvalidBookException(String message) {
        super(message);
    }
}
