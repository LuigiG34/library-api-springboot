package com.example.libraryapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.libraryapi.dto.errors.ErrorResponse;
import com.example.libraryapi.exception.books.BookNotFoundException;
import com.example.libraryapi.exception.books.InvalidBookException;
import com.example.libraryapi.exception.books.InvalidSortByException;
import com.example.libraryapi.exception.loans.LoanNotFoundException;
import com.example.libraryapi.exception.members.InvalidMemberException;
import com.example.libraryapi.exception.members.MemberNotFoundException;
import com.example.libraryapi.exception.parent.ConflictException;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler({
        BookNotFoundException.class,
        MemberNotFoundException.class,
        LoanNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }

    @ExceptionHandler({
        InvalidBookException.class,
        InvalidMemberException.class,
        InvalidSortByException.class,
        MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(Exception ex) {
        return new ErrorResponse(400, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException ex) {
        return new ErrorResponse(409, ex.getMessage());
    }
}
