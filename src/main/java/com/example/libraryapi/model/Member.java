package com.example.libraryapi.model;

import java.time.LocalDate;

public class Member {
    private Long id;
    private String name;
    private String email;
    private LocalDate inscriptionDate;

    public Member(Long id, String name, String email, LocalDate inscriptionDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.inscriptionDate = inscriptionDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getInscriptionDate() {
        return inscriptionDate;
    }

    public void setInscriptionDate(LocalDate inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }
}
