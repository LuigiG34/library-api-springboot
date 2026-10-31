package com.example.libraryapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.libraryapi.dto.loans.CreateLoanRequest;
import com.example.libraryapi.dto.loans.LoanResponse;
import com.example.libraryapi.model.Loan;
import com.example.libraryapi.service.LoanService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping ("/api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse createLoan(@RequestBody CreateLoanRequest loanRequest) {
        Loan loan = this.loanService.createLoan(loanRequest.bookId(), loanRequest.memberId());
        return this.toResponse(loan);
    }
    
    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    public LoanResponse returnLoan(@PathVariable("id") Long id) {
        Loan loan = this.loanService.returnLoan(id);
        return this.toResponse(loan);
    }

    @GetMapping("/overdue")
    @ResponseStatus(HttpStatus.OK)
    public List<LoanResponse> getMethodName(@RequestParam String param) {
        return this.loanService.getOverdueLoans().stream()
                .map(loan -> this.toResponse(loan))
                .collect(Collectors.toList());
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
            loan.getId(),
            loan.getBookId(),
            loan.getMemberId(),
            loan.getLoanDate(),
            loan.getReturnDate(),
            loan.getReturnedDate(),
            loan.isOngoing(),
            loan.isOverdue()
        );
    }
}
