package com.example.libraryapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'integration HTTP de Library API (Spring Boot 4.x / MockMvc).
 * Les tests partent des fixtures : 8 livres, 3 adherents et 0 emprunt.
 * Chaque methode redemarre un contexte propre pour eviter les dependances entre tests.
 */
@SpringBootTest(properties = "library.maxLoansPerMember=3")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LibraryApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void catalogueEtFiltres() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)));

        mvc.perform(get("/api/books/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dune"))
                .andExpect(jsonPath("$.availableCopies").value(1));

        mvc.perform(get("/api/books")
                        .param("genre", "ROMAN")
                        .param("available", "true")
                        .param("sort", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Les Misérables"));

        mvc.perform(get("/api/books").param("author", "hugo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Martin"));

        mvc.perform(get("/api/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void erreursDeLectureEtDeConversion() throws Exception {
        mvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mvc.perform(get("/api/members/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mvc.perform(post("/api/loans/999/return"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        mvc.perform(get("/api/books/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(get("/api/books").param("sort", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(get("/api/books").param("genre", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void creationModificationSuppressionEtValidationLivre() throws Exception {
        long bookId = createBook("9780132350884", "Clean Code", "Robert C. Martin", 2008, "ESSAI", 2);

        mvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9780132350884", "Duplicate", "Test Author", 2020, "ROMAN", 2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9781111111111", "", "Test Author", 2020, "ROMAN", 2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9782222222222", "Future Book", "Test Author", 2099, "ROMAN", 2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9783333333333", "Zero Copies", "Test Author", 2020, "ROMAN", 0)))
                .andExpect(status().isBadRequest());

        mvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9780132350884", "Clean Code - Updated", "Robert C. Martin", 2008, "ESSAI", 5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code - Updated"))
                .andExpect(jsonPath("$.totalCopies").value(5))
                .andExpect(jsonPath("$.availableCopies").value(5));

        mvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void creationAdherentEtUniciteEmail() throws Exception {
        long memberId = createMember("Lucas Dupont", "lucas.dupont@example.com");

        mvc.perform(get("/api/members/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("lucas.dupont@example.com"));

        mvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("Autre Lucas", " LUCAS.DUPONT@example.com ")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson("   ", "new.member@example.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(get("/api/members/{id}/loans", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void empruntExemplaireUniqueEtRetour() throws Exception {
        long bookId = createBook("9784444444444", "Livre unique", "Test Auteur", 2020, "ROMAN", 1);
        long alice = createMember("Test Alice", "test.alice@example.com");
        long bob = createMember("Test Bob", "test.bob@example.com");

        long loanId = createLoan(bookId, alice);
        mvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(0));

        mvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson(bookId, bob)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(get("/api/members/{id}/loans", alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].onGoing").value(true));

        mvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/loans/{id}/return", loanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onGoing").value(false))
                .andExpect(jsonPath("$.returnedDate").exists());

        mvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(1));

        mvc.perform(post("/api/loans/{id}/return", loanId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(get("/api/members/{id}/loans", alice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void limiteTroisEmpruntsEtEmpruntEnDouble() throws Exception {
        long memberId = createMember("Test Lecteur", "lecteur@example.com");
        long b1 = createBook("9785555555551", "Livre A", "Auteur", 2020, "ROMAN", 2);
        long b2 = createBook("9785555555552", "Livre B", "Auteur", 2020, "ROMAN", 2);
        long b3 = createBook("9785555555553", "Livre C", "Auteur", 2020, "ROMAN", 2);
        long b4 = createBook("9785555555554", "Livre D", "Auteur", 2020, "ROMAN", 2);

        createLoan(b1, memberId);

        mvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson(b1, memberId)))
                .andExpect(status().isConflict());

        createLoan(b2, memberId);
        createLoan(b3, memberId);

        mvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson(b4, memberId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(get("/api/members/{id}/loans", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void impossibleDeReduireLeStockSousLesEmprunts() throws Exception {
        long bookId = createBook("9786666666666", "Livre partagé", "Auteur", 2020, "ROMAN", 3);
        long m1 = createMember("Lecteur Un", "un@example.com");
        long m2 = createMember("Lecteur Deux", "deux@example.com");

        createLoan(bookId, m1);
        createLoan(bookId, m2);

        mvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9786666666666", "Livre partagé", "Auteur", 2020, "ROMAN", 1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson("9786666666666", "Livre partagé", "Auteur", 2020, "ROMAN", 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCopies").value(0));

        mvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isConflict());
    }

    private long createBook(String isbn, String title, String author, int year, String genre, int copies)
            throws Exception {
        MvcResult result = mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson(isbn, title, author, year, genre, copies)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/books/")))
                .andExpect(jsonPath("$.availableCopies").value(copies))
                .andReturn();
        return idFromJson(result);
    }

    private long createMember(String name, String email) throws Exception {
        MvcResult result = mvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberJson(name, email)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/members/")))
                .andReturn();
        return idFromJson(result);
    }

    private long createLoan(long bookId, long memberId) throws Exception {
        MvcResult result = mvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loanJson(bookId, memberId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.onGoing").value(true))
                .andReturn();
        return idFromJson(result);
    }

    private long idFromJson(MvcResult result) throws Exception {
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private String bookJson(String isbn, String title, String author, int year, String genre, int copies) {
        return """
                {
                  "isbn": "%s",
                  "title": "%s",
                  "author": "%s",
                  "publicationYear": %d,
                  "genre": "%s",
                  "totalCopies": %d
                }
                """.formatted(isbn, title, author, year, genre, copies);
    }

    private String memberJson(String name, String email) {
        return """
                {"name": "%s", "email": "%s"}
                """.formatted(name, email);
    }

    private String loanJson(long bookId, long memberId) {
        return """
                {"bookId": %d, "memberId": %d}
                """.formatted(bookId, memberId);
    }
}
