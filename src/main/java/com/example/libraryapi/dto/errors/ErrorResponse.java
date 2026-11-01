package com.example.libraryapi.dto.errors;

public record ErrorResponse(
    int status,
    String message
) {}
