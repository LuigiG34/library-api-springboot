package com.example.libraryapi.dto.members;

import java.time.LocalDate;

public record MemberResponse(
    Long id,
    String name,
    String email,
    LocalDate inscriptionDate
) {}