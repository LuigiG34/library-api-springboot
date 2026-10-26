package com.example.libraryapi.repository;

import java.util.List;
import java.util.Optional;

import com.example.libraryapi.model.Loan;

public interface LoanRepository {
    
    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    List<Loan> findLoansByMemberId(Long memberId);

    List<Loan> findLoansByBookId(Long bookId);

    List<Loan> findAll();
}
