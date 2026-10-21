package com.example.libraryapi.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.libraryapi.model.Member;

@Repository 
public class InMemoryMemberRepository implements MemberRepository {

    private final Map<Long, Member> members = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            member.setId(sequence.incrementAndGet());
        }
        members.put(member.getId(), member);

        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override 
    public Optional<Member> findByEmail(String email) {
        return members.values().stream()
               .filter(member -> member.getEmail().equals(email))
               .findFirst();
    }

    @Override 
    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }
    
}
