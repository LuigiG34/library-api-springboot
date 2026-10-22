package com.example.libraryapi.exception.members;

public class MembersNotFoundException extends RuntimeException {
    public MembersNotFoundException(String message) {
        super(message);
    }
}
