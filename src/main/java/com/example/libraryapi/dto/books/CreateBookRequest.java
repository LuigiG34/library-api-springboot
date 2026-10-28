package com.example.libraryapi.dto.books;

import com.example.libraryapi.model.Genre;

public record CreateBookRequest(
    String isbn,
    String title,
    String author,
    int publicationYear,
    Genre genre,
    int totalCopies
) {}
