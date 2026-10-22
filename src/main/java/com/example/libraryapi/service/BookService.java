package com.example.libraryapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.libraryapi.exception.BookInUseException;
import com.example.libraryapi.exception.BookNotFoundException;
import com.example.libraryapi.exception.DuplicateIsbnException;
import com.example.libraryapi.exception.InvalidBookException;
import com.example.libraryapi.exception.InvalidSortByException;
import com.example.libraryapi.model.Book;
import com.example.libraryapi.model.Genre;
import com.example.libraryapi.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book getBookById(Long id) {
        return this.bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    public List<Book> getAllBooks() {
        return this.bookRepository.findAll();
    }

    public Book createBook(Book book) {
        validateBook(book);

        // Check if ISBN is unique
        if (this.bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateIsbnException("Book with ISBN " + book.getIsbn() + " already exists");
        }

        // Available copies should be equal to total copies when creating a new book
        book.setAvailableCopies(book.getTotalCopies());

        return this.bookRepository.save(book);
    }

    public Book updateBook(Long id, Book updatedBook) {
        Book existingBook = this.bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        validateBook(updatedBook);

        // Check if ISBN is unique & not the same as the existing book's ISBN
        Book bookWithSameIsbn = this.bookRepository.findByIsbn(updatedBook.getIsbn()).orElse(null);
        if (bookWithSameIsbn != null && !bookWithSameIsbn.getId().equals(id)) {
            throw new DuplicateIsbnException("Book with ISBN " + updatedBook.getIsbn() + " already exists");
        }

        // Recuperate the amount of borrowed copies before updating the book
        int borrowedCopies = existingBook.getTotalCopies() - existingBook.getAvailableCopies();

        // Verify that the new total copies is not less than the borrowed copies
        if (updatedBook.getTotalCopies() < borrowedCopies) {
            throw new BookInUseException(
                    "Total copies cannot be less than the number of borrowed copies (" + borrowedCopies + ")");
        }

        // Update the existing book's properties
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPublicationYear(updatedBook.getPublicationYear());
        existingBook.setTotalCopies(updatedBook.getTotalCopies());
        existingBook.setGenre(updatedBook.getGenre());

        // Update available copies based on the new total copies and borrowed copies
        existingBook.setAvailableCopies(updatedBook.getTotalCopies() - borrowedCopies);

        return this.bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        Book existingBook = this.bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

        // If at least one copy of the book is borrowed, we cannot delete it
        if (existingBook.getAvailableCopies() < existingBook.getTotalCopies()) {
            throw new BookInUseException("Cannot delete book with borrowed copies");
        }

        this.bookRepository.deleteById(existingBook.getId());
    }

    public List<Book> getFilteredBooks(Genre genre, String author, boolean available, String sortBy) {
        // Get all books then use streams to combine filters, some may be null, if all are null return all books
        List<Book> books = this.bookRepository.findAll();
        if (genre != null) {
            books = books.stream().filter(book -> book.getGenre() == genre).toList();
        }
        // For authors we must partially match, for example : "hugo" will find books with "Victor Hugo" as author
        if (author != null) {
            books = books.stream().filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase())).toList();
        }
        if (available) {
            books = books.stream().filter(book -> book.getAvailableCopies() > 0).toList();
        }

        // We can sortBy title or year, if sort is not title or year or null, we throw an exception
        if (sortBy != null) {
            if (sortBy.equalsIgnoreCase("title")) {
                books = books.stream().sorted((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle())).toList();
            } else if (sortBy.equalsIgnoreCase("year")) {
                books = books.stream()
                        .sorted((b1, b2) -> Integer.compare(b1.getPublicationYear(), b2.getPublicationYear())).toList();
            } else {
                throw new InvalidSortByException("Invalid sort parameter: " + sortBy);
            }
        }

        // If nothing corresponds to the filters, we return an empty list
        return books;
    }

    private void validateBook(Book book) {
        // Check if title is empty or only whitespace
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidBookException("Book title cannot be empty");
        }

        // Check if author is empty or only whitespace
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidBookException("Book author cannot be empty");
        }

        // Check if ISBN is empty or only whitespace
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new InvalidBookException("Book ISBN cannot be empty");
        }

        // Check if publication year is not superior to the current year
        int currentYear = java.time.Year.now().getValue();
        if (book.getPublicationYear() > currentYear) {
            throw new InvalidBookException("Book publication year cannot be in the future");
        }

        // Check if total copies is not negative
        if (book.getTotalCopies() <= 0) {
            throw new InvalidBookException("Book total copies cannot be negative or equal to 0");
        }
    }
}
