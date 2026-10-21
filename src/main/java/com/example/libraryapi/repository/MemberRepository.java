package com.example.libraryapi.repository;

import java.util.List;
import java.util.Optional;

import com.example.libraryapi.model.Member;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    List<Member> findAll();
}
