package com.example.libraryapi.dto.loans;

import java.time.LocalDate;

public record LoanResponse(
    Long id,
    Long bookId,
    Long memberId,
    LocalDate loanDate,
    LocalDate returnDate,
    LocalDate returnedDate,
    boolean onGoing,
    boolean overdue
) {}
