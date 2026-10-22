package com.example.libraryapi.exception.members;

public class InvalidMemberException extends IllegalArgumentException {
    public InvalidMemberException(String message) {
        super(message);
    }
}