package com.example.libraryapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.libraryapi.model.Loan;
import com.example.libraryapi.model.Book;
import com.example.libraryapi.repository.LoanRepository;
import com.example.libraryapi.repository.BookRepository;
import com.example.libraryapi.repository.MemberRepository;
import com.example.libraryapi.exception.books.BookInUseException;
import com.example.libraryapi.exception.books.BookNotFoundException;
import com.example.libraryapi.exception.loans.LoanIsAlreadyReturnedException;
import com.example.libraryapi.exception.loans.LoanNotFoundException;
import com.example.libraryapi.exception.loans.NumberOfLoansExceededException;
import com.example.libraryapi.exception.members.MemberNotFoundException;

@Service 
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final int maxLoansPerMember;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, MemberRepository memberRepository, @Value("${library.maxLoansPerMember}") int maxLoansPerMember) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.maxLoansPerMember = maxLoansPerMember;
    }

    public synchronized Loan createLoan(Long bookId, Long memberId) {
        // Verify book exists else throw exception
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        // Verify member exists else throw exception
        this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));   
        
        // Verify at least 1 book is available else throw exception conflict
        if (book.getAvailableCopies() < 1) {
            throw new BookInUseException("No available copies of the book");
        }

        // Verify that member has not exceeded the maximum number of loans (3) else throw exception conflict
        if (this.loanRepository.findLoansByMemberId(memberId).size() >= this.maxLoansPerMember) {
            throw new NumberOfLoansExceededException("Member has exceeded the maximum number of loans (" + this.maxLoansPerMember + ")");
        }

        // Verify that member doesnt have this current book loabed else throw exception conflict
        if (this.loanRepository.findLoansByBookId(bookId).stream()
                .anyMatch(loan -> loan.getMemberId().equals(memberId))
            ) {
            throw new BookInUseException("Member already has this book loaned");
        }

        // Create a new loan
        Loan loan = new Loan(
            null, 
            bookId, 
            memberId, 
            java.time.LocalDate.now(), 
            java.time.LocalDate.now().plusWeeks(2), 
            null
        );
        this.loanRepository.save(loan);

        // Decrement the available copies of the book
        book.decrementAvailableCopies();
        this.bookRepository.save(book);

        return loan;
    }
    
    public synchronized Loan returnLoan(Long loanId) {
        Loan loan = this.loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        // Verify loan is still onGoing else throw exception conflict
        if (!loan.isOngoing()) {
            throw new LoanIsAlreadyReturnedException("Loan has already been returned");
        }

        // Get book and increment the available copies
        Book book = this.bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found"));
        book.incrementAvailableCopies();
        this.bookRepository.save(book);

        // Save return date
        loan.setReturnedDate(java.time.LocalDate.now());
        this.loanRepository.save(loan);

        return loan;
    }

    public List<Loan> getActiveLoansByMemberId(Long memberId) {
        // Verify member exists else throw exception
        this.memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));

        return this.loanRepository.findLoansByMemberId(memberId).stream()
                .filter(Loan::isOngoing)
                .toList();
    }

    public List<Loan> getOverdueLoans() {
        return this.loanRepository.findAll().stream()
                .filter(loan -> loan.isOverdue())
                .toList();
    }
}
