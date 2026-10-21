package com.example.libraryapi.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.libraryapi.model.Loan;

@Repository 
public class InMemoryLoanRepository implements LoanRepository {

    private final Map<Long, Loan> loans = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Loan save(Loan loan) {
        if (loan.getId() == null) {
            loan.setId(sequence.incrementAndGet());
        }
        loans.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return Optional.ofNullable(loans.get(id));
    }

    @Override
    public List<Loan> findLoansByMemberId(Long memberId) {
        return loans.values().stream()
               .filter(loan -> loan.getMemberId().equals(memberId) && loan.isOngoing())
               .collect(Collectors.toList());
    }

    @Override
    public List<Loan> findLoansByBookId(Long bookId) {
        return loans.values().stream()
               .filter(loan -> loan.getBookId().equals(bookId) && loan.isOngoing())
               .collect(Collectors.toList());
    }
    
}
