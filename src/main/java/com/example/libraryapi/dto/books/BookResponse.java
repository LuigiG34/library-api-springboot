package com.example.libraryapi.dto.books;

import com.example.libraryapi.model.Genre;

public record BookResponse(
    Long id,
    String isbn,
    String title,
    String author,
    int publicationYear,
    Genre genre,
    int totalCopies,
    int availableCopies
) {}