package com.example.libraryapi.repository;

import java.util.List;
import java.util.Optional;

import com.example.libraryapi.model.Book;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAll();

    void deleteById(Long id);
}
