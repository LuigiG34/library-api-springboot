package com.example.libraryapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.libraryapi.model.Book;
import com.example.libraryapi.model.Genre;
import com.example.libraryapi.model.Member;
import com.example.libraryapi.service.BookService;
import com.example.libraryapi.service.MemberService;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookService bookService;
    private final MemberService memberService;

    public DataInitializer(BookService bookService, MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @Override
    public void run(String... args) throws Exception {
        // =========================
        // BOOKS
        // =========================

        bookService.createBook(new Book(
                null, "9780000000002", "Les Misérables",
                "Victor Hugo", 1862, Genre.ROMAN, 3, 3));

        bookService.createBook(new Book(
                null, "9780000000019", "Notre-Dame de Paris",
                "Victor Hugo", 1831, Genre.ROMAN, 2, 2));

        bookService.createBook(new Book(
                null, "9780000000026", "Dune",
                "Frank Herbert", 1965, Genre.SCIENCE_FICTION, 1, 1));

        bookService.createBook(new Book(
                null, "9780000000033", "Fondation",
                "Isaac Asimov", 1951, Genre.SCIENCE_FICTION, 4, 4));

        bookService.createBook(new Book(
                null, "9780000000040", "Le Crime de l'Orient-Express",
                "Agatha Christie", 1934, Genre.POLICIER, 2, 2));

        bookService.createBook(new Book(
                null, "9780000000057", "Le Chien des Baskerville",
                "Arthur Conan Doyle", 1902, Genre.POLICIER, 3, 3));

        bookService.createBook(new Book(
                null, "9780000000064", "Le Mythe de Sisyphe",
                "Albert Camus", 1942, Genre.ESSAI, 2, 2));

        bookService.createBook(new Book(
                null, "9780000000071", "Le Petit Prince",
                "Antoine de Saint-Exupéry", 1943, Genre.JEUNESSE, 5, 5));

        // =========================
        // MEMBERS
        // =========================

        memberService.createMember(new Member(
                null, "Alice Martin", "alice.martin@example.com", null));

        memberService.createMember(new Member(
                null, "Thomas Bernard", "thomas.bernard@example.com", null));

        memberService.createMember(new Member(
                null, "Emma Dubois", "emma.dubois@example.com", null));
    }
}
