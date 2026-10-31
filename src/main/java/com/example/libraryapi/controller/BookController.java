package com.example.libraryapi.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.libraryapi.dto.books.BookResponse;
import com.example.libraryapi.dto.books.CreateBookRequest;
import com.example.libraryapi.dto.books.UpdateBookRequest;
import com.example.libraryapi.model.Book;
import com.example.libraryapi.model.Genre;
import com.example.libraryapi.service.BookService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController 
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponse> getBooks( 
        @RequestParam(required = false) Genre genre, 
        @RequestParam(required = false) String author, 
        @RequestParam(required = false, defaultValue = "false") boolean available,
        @RequestParam(required = false) String sort
    ) {
        return this.bookService.getFilteredBooks(genre, author, available, sort).stream()
                .map(book -> this.toResponse(book))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse getBookById(@PathVariable("id") Long id) {
        Book book = this.bookService.getBookById(id);
        return this.toResponse(book);
    }
    
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBookRequest bookRequest) {
        // Convert Request to Entity
        Book book = new Book(
            null,
            bookRequest.isbn(),
            bookRequest.title(),
            bookRequest.author(),
            bookRequest.publicationYear(),
            bookRequest.genre(),
            bookRequest.totalCopies(),
            bookRequest.totalCopies() 
        );
        
        Book savedBook = this.bookService.createBook(book);

        BookResponse bookResponse = this.toResponse(savedBook);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedBook.getId())
            .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>(bookResponse, headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable("id") Long id, @RequestBody UpdateBookRequest bookRequest) {

        // Convert Request to Entity
        Book book = new Book(
            null,
            bookRequest.isbn(),
            bookRequest.title(),
            bookRequest.author(),
            bookRequest.publicationYear(),
            bookRequest.genre(),
            bookRequest.totalCopies(),
            bookRequest.totalCopies() 
        );
            
        Book savedBook = this.bookService.updateBook(id, book);
            
        return this.toResponse(savedBook);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        this.bookService.deleteBook(id);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
            book.getId(), 
            book.getIsbn(), 
            book.getTitle(), 
            book.getAuthor(), 
            book.getPublicationYear(), 
            book.getGenre(), 
            book.getTotalCopies(), 
            book.getAvailableCopies()
        );
    }
}
