package com.example.libraryapi.dto.members;

public record CreateMemberRequest(
    String name,
    String email
) {}
