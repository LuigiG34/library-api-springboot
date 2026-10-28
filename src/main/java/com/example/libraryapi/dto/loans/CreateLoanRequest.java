package com.example.libraryapi.dto.loans;

public record CreateLoanRequest(
    Long bookId,
    Long memberId
){}
