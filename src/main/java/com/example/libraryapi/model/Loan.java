package com.example.libraryapi.model;

import java.time.LocalDate;

public class Loan {
    private Long id;
    private Long bookId;
    private Long memberId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private LocalDate returnedDate;

    public Loan(Long id, Long bookId, Long memberId, LocalDate loanDate, LocalDate returnDate, LocalDate returnedDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.returnedDate = returnedDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    // is loan still on going (not returned yet)
    public boolean isOngoing() {
        return this.returnedDate == null;
    }

    // is loan overdue (not returned yet and return date is before today)
    public boolean isOverdue() {
        return this.isOngoing() && this.returnDate.isBefore(LocalDate.now());
    }
}
